/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.repository.auth.internal

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.RSASSAVerifier
import net.meatplatform.sandbox.annotation.OpenForTesting
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.exception.external.auth.ProviderAuthenticationFailedException
import net.meatplatform.sandbox.http.SandboxHttpClient
import java.math.BigInteger
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec
import java.text.ParseException
import java.time.Instant
import java.util.*

/**
 * 참고 문서: [Sign in with Apple REST API](https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_rest_api)
 *
 * @since 2023-05-07
 */
@OpenForTesting
internal class AppleOAuthVerifier(
    private val oAuthUrl: String,
    private val httpClient: SandboxHttpClient,
    private val objectMapper: ObjectMapper
) {
    @Suppress("ThrowsCount")    // 억지로 줄이려고 함수 단위로 쪼개면 읽기 더 어렵다.
    fun verify(token: String): ProviderAuthentication {
        val parsedToken = try {
            JWSObject.parse(token)
        } catch (e: ParseException) {
            throw paFailedException(token, "Failed to parse JWT token.", e)
        }

        val response = httpClient.get(url = oAuthUrl)

        val responseBody = response.body
        if (response.code == 200 && responseBody != null) {
            val appleKeyResponse = responseBody.use {
                objectMapper.readValue(it, ApplePublicKeysResponse::class.java)
            }

            val parsedJwt = verifyJwt(parsedToken, appleKeyResponse)

            return ProviderAuthentication.create(
                ProviderAuthentication.Type.APPLE,
                parsedJwt.subject
            )
        } else {
            val message = if (responseBody == null) {
                "Empty response from Apple OAuth server."
            } else {
                "Unexpected response from Apple OAuth server: ${response.code}"
            }

            throw ProviderAuthenticationFailedException(ProviderAuthentication.Type.GOOGLE, token, message)
        }
    }

    @Suppress("ThrowsCount")    // 억지로 줄이려고 함수 단위로 쪼개면 읽기 더 어렵다.
    private fun verifyJwt(appleJwtToken: JWSObject, keysResponse: ApplePublicKeysResponse): AppleJwtPayload {
        fun paFailedException(message: String) = paFailedException(
            token = appleJwtToken.payload.toString(), message = message
        )

        // modulus, exponent 리스트중 idToken 에 해당하는 항목 선택, 없으면 예외 발생
        val selectedKey = keysResponse.keys.singleOrNull {
            it.alg == appleJwtToken.header.algorithm.name && it.kid == appleJwtToken.header.keyID
        } ?: throw paFailedException("The header information for authentication does not match.")

        val nByte = Base64.getUrlDecoder().decode(selectedKey.n)
        val eByte = Base64.getUrlDecoder().decode(selectedKey.e)
        val n = BigInteger(1, nByte)
        val e = BigInteger(1, eByte)
        val publicKey = KeyFactory
            .getInstance("RSA")
            .generatePublic(RSAPublicKeySpec(n, e)) as RSAPublicKey

        // 토큰의 서명을 확인하여 애플이 발급한 토큰이 아닐 경우 예외 발생
        if (!appleJwtToken.verify(RSASSAVerifier(publicKey))) {
            throw paFailedException("RSA signatures do not match.")
        }
        val payload = AppleJwtPayload.fromBy(appleJwtToken.payload, objectMapper)

        if (Instant.now() >= payload.expiredAt) {
            throw paFailedException("Token is expired.")
        }

        return payload
    }

    private fun paFailedException(
        token: String,
        message: String,
        cause: Throwable? = null
    ) = ProviderAuthenticationFailedException(
        providerType = ProviderAuthentication.Type.APPLE,
        providerToken = token,
        message = message,
        cause = cause
    )

    @JsonDeserialize
    private data class ApplePublicKeysResponse(val keys: List<ApplePublicKeyResponse>)

    @JsonDeserialize
    private data class ApplePublicKeyResponse(
        val kty: String,
        val kid: String,
        val use: String,
        val alg: String,
        val n: String,
        val e: String
    )

    private data class AppleJwtPayload(
        val issuer: String,
        val subject: String,
        val audience: String,
        val expiredAt: Instant,
        val issuedAt: Instant,
        val cHash: String,
        val email: String,
        val emailVerified: Boolean,
        val isPrivateEmail: Boolean,
        val authTime: Instant,
        val nonceSupported: Boolean
    ) {
        companion object {
            private const val ISSUER = "iss"
            private const val AUDIENCE = "aud"
            private const val EXPIRATION = "exp"
            private const val ISSUED_AT = "iat"
            private const val SUBJECT = "sub"
            private const val C_HASH = "c_hash"
            private const val EMAIL = "email"
            private const val EMAIL_VERIFIED = "email_verified"
            private const val IS_PRIVATE_EMAIL = "is_private_email"
            private const val AUTH_TIME = "auth_time"
            private const val NONCE_SUPPORTED = "nonce_supported"

            fun fromBy(payload: Payload, objectMapper: ObjectMapper): AppleJwtPayload {
                val jsonStr = payload.toString()
                val mapTypeRef = object : TypeReference<Map<String, Any>>() {}

                return objectMapper.readValue(jsonStr, mapTypeRef).run {
                    AppleJwtPayload(
                        issuer = get(ISSUER) as? String ?: "",
                        subject = get(SUBJECT) as? String ?: "",
                        audience = get(AUDIENCE) as? String ?: "",
                        expiredAt = Instant.ofEpochSecond((get(EXPIRATION) as Number).toLong()),
                        issuedAt = Instant.ofEpochSecond((get(ISSUED_AT) as Number).toLong()),
                        cHash = get(C_HASH) as? String ?: "",
                        email = get(EMAIL) as? String ?: "",
                        emailVerified = (get(EMAIL_VERIFIED) as? String)?.toBoolean() ?: false,
                        isPrivateEmail = (get(IS_PRIVATE_EMAIL) as? String)?.toBoolean() ?: false,
                        authTime = Instant.ofEpochSecond((get(AUTH_TIME) as Number).toLong()),
                        nonceSupported = get(NONCE_SUPPORTED) as? Boolean ?: false
                    )
                }
            }
        }
    }
}
