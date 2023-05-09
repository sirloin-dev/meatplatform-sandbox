/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.security.authentication

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.sirloin.jvmlib.annotation.VisibleForTesting
import com.sirloin.jvmlib.util.FastCollectedLruCache
import jakarta.servlet.http.HttpServletRequest
import net.meatplatform.sandbox.appconfig.security.AuthenticationPolicy
import net.meatplatform.sandbox.domain.auth.AccessTokenPayload
import net.meatplatform.sandbox.domain.auth.AuthenticationTokenPayload
import net.meatplatform.sandbox.domain.auth.repository.RsaCertificateRepository
import net.meatplatform.sandbox.exception.external.auth.AuthenticationExpiredException
import net.meatplatform.sandbox.exception.external.auth.AuthenticationRequiredException
import net.meatplatform.sandbox.exception.external.auth.InvalidAuthenticationTokenException
import org.slf4j.Logger
import org.springframework.context.annotation.Scope
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationManagerResolver
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import java.text.ParseException
import java.time.Instant
import java.util.*

/**
 * Spring boot 3.0(Spring Security 6) 부터는 여기서 생성한 [Authentication] 객체를 `"request"` [Scope] 내에서
 * DI 로 주입받을 수 있다.
 *
 * 또한 생성한 [Authentication] 객체는 [SecurityContextHolder] 에 저장된다. 따라서 이후 Spring land 의 코드들은
 * [SecurityContextHolder] 에 접근해 [Authentication] 객체를 얻을 수 있다.
 *
 * @since 2022-03-20
 */
@Component
class JwtAuthenticationProvider(
    private val policy: AuthenticationPolicy,
    private val objectMapper: ObjectMapper,
    private val rsaCerts: RsaCertificateRepository,
    private val log: Logger
) : AuthenticationManagerResolver<HttpServletRequest>, AuthenticationManager, AuthenticationProvider {
    @VisibleForTesting
    internal val verifiedTokens: FastCollectedLruCache<UUID, VerifiedAuthentication>? =
        if (policy.validationCacheCount < 1) {
            null
        } else {
            FastCollectedLruCache.create(policy.validationCacheCount)
        }

    override fun resolve(context: HttpServletRequest?): AuthenticationManager = this

    override fun authenticate(authentication: Authentication?): Authentication {
        // 이 타이밍에는 반드시 있지만 논리 완결을 위해 null 시 예외를 발생시킨다.
        val httpAuthToken = authentication as? BearerTokenAuthenticationToken ?: throw AuthenticationRequiredException()

        // We don't need to set this object into SecurityContextHolder, because Spring will do it for us.
        return verify(httpAuthToken)
    }

    override fun supports(authentication: Class<*>?): Boolean = authentication?.let {
        BearerTokenAuthenticationToken::class.java.isAssignableFrom(it)
    } ?: false

    private fun verify(authenticationToken: BearerTokenAuthenticationToken): Authentication {
        val parsedToken = try {
            JWSObject.parse(authenticationToken.principal.toString())
        } catch (e: ParseException) {
            throw InvalidAuthenticationTokenException(cause = e)
        }

        val jsonStr = parsedToken.payload.toString()
        val mapTypeRef = object : TypeReference<Map<String, Any>>() {}
        val tokenPayloadAsMap = objectMapper.readValue(jsonStr, mapTypeRef)
        val unverifiedAuthentication = AuthenticationTokenPayload.deserialise(
            tokenPayloadAsMap,
            policy.accessTokenLifeInSeconds,
            policy.refreshTokenLifeInSeconds
        ) ?: run {
            log.warn("Invalid token payload: {}", jsonStr)
            throw InvalidAuthenticationTokenException("Invalid token payload")
        }
        val userId = try {
            UUID.fromString(unverifiedAuthentication.subject)
        } catch (e: IllegalArgumentException) {
            throw InvalidAuthenticationTokenException("Unknown user id: ${unverifiedAuthentication.subject}", e)
        }

        verifiedTokens?.let { cache ->
            synchronized(cache) { cache.get(userId)?.takeIf { Instant.now() < it.expiredAt } }?.let { return it }
        }

        val cert = try {
            rsaCerts.getById(UUID.fromString(unverifiedAuthentication.certificateId))
        } catch (e: IllegalArgumentException) {
            throw InvalidAuthenticationTokenException("Unknown keyId: ${unverifiedAuthentication.certificateId}", e)
        }

        if (parsedToken.verify(RSASSAVerifier(cert.publicKey))) {
            if (Instant.now() >= unverifiedAuthentication.expiredAt) {
                verifiedTokens?.remove(userId)
                log.debug("This token is expired: {}", unverifiedAuthentication)
                throw AuthenticationExpiredException()
            }

            val verifiedAuthentication = VerifiedAuthentication.from(unverifiedAuthentication)
            if (unverifiedAuthentication is AccessTokenPayload) {
                verifiedTokens?.put(verifiedAuthentication.userId, verifiedAuthentication)
            }

            return verifiedAuthentication
        } else {
            log.warn("Malicious JWT token is detected: {}", unverifiedAuthentication)
            throw HttpClientErrorException(HttpStatus.UNAUTHORIZED)
        }
    }
}
