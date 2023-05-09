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
import net.meatplatform.sandbox.appconfig.security.AuthenticationPolicy
import net.meatplatform.sandbox.domain.auth.AccessTokenPayload
import net.meatplatform.sandbox.domain.auth.AuthenticationTokenPayload
import net.meatplatform.sandbox.domain.auth.repository.RsaCertificateRepository
import net.meatplatform.sandbox.exception.external.auth.AuthenticationExpiredException
import net.meatplatform.sandbox.exception.external.auth.InvalidAuthenticationTokenException
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import java.text.ParseException
import java.time.Instant
import java.util.*

/**
 * @since 2022-03-20
 */
interface AuthenticationTokenVerifier {
    fun verify(authenticationToken: HttpAuthenticationToken): Authentication
}

internal class AuthenticationTokenVerifierImpl(
    private val policy: AuthenticationPolicy,
    private val objectMapper: ObjectMapper,
    private val rsaCerts: RsaCertificateRepository,
    private val log: Logger
) : AuthenticationTokenVerifier {
    @VisibleForTesting
    internal val verifiedTokens: FastCollectedLruCache<UUID, VerifiedAuthentication>? =
        if (policy.validationCacheCount < 1) {
            null
        } else {
            FastCollectedLruCache.create(policy.validationCacheCount)
        }

    override fun verify(authenticationToken: HttpAuthenticationToken): Authentication {
        val parsedToken = try {
            JWSObject.parse(authenticationToken.credentials)
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
