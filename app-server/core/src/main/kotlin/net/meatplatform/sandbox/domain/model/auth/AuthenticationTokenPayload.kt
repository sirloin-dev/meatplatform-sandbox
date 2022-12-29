/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.model.auth

import java.time.Instant

/**
 * @since 2022-12-27
 */
sealed interface AuthenticationTokenPayload {
    val certificate: RsaCertificate

    val issuer: String

    val audience: String

    val subject: String

    val issuedAt: Instant

    val expiredAt : Instant

    fun serialise(): Map<String, Any> = HashMap<String, Any>().apply {
        put(SERIALISE_KEY_KEY_ID, certificate.id.toString())
        put(SERIALISE_KEY_ISSUER, issuer)
        put(SERIALISE_KEY_AUDIENCE, audience)
        put(SERIALISE_KEY_SUBJECT, subject)
        put(SERIALISE_KEY_ISSUED_AT, issuedAt.epochSecond)
        put(SERIALISE_KEY_EXPIRED_AT, expiredAt.epochSecond)
    }

    companion object {
        const val SERIALISE_KEY_KEY_ID = "kid"
        const val SERIALISE_KEY_ISSUER = "iss"
        const val SERIALISE_KEY_AUDIENCE = "aud"
        const val SERIALISE_KEY_SUBJECT = "sub"
        const val SERIALISE_KEY_ISSUED_AT = "iss"
        const val SERIALISE_KEY_EXPIRED_AT = "exp"
    }
}
