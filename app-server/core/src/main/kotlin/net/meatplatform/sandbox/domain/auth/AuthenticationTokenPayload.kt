/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.auth

import java.time.Duration
import java.time.Instant

/**
 * @since 2022-12-27
 */
sealed interface AuthenticationTokenPayload {
    val certificateId: String

    val issuer: String

    val audience: String

    val subject: String

    val issuedAt: Instant

    val expiredAt: Instant

    fun serialise(): Map<String, Any> = HashMap<String, Any>().apply {
        put(SERIALISE_KEY_KEY_ID, certificateId)
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
        const val SERIALISE_KEY_ISSUED_AT = "iat"
        const val SERIALISE_KEY_EXPIRED_AT = "exp"

        fun deserialise(
            serialisedMap: Map<String, Any>,
            accessTokenLifeSeconds: Long,
            refreshTokenLifeSeconds: Long
        ): AuthenticationTokenPayload? {
            val (issuedAt, expiredAt) = serialisedMap.extractTimestamps() ?: return null

            return when (Duration.between(issuedAt, expiredAt).seconds) {
                accessTokenLifeSeconds -> AccessTokenPayload.deserialise(serialisedMap)
                refreshTokenLifeSeconds -> RefreshTokenPayload.deserialise(serialisedMap)
                else -> null
            }
        }

        @Suppress("ReturnCount")    // Early return 으로 불필요한 가정을 제거해 가독성을 높인다.
        internal fun Map<String, Any>.parse(): AuthenticationTokenPayload? {
            val certificateId = get(SERIALISE_KEY_KEY_ID) as? String ?: return null
            val issuer = get(SERIALISE_KEY_ISSUER) as? String ?: return null
            val audience = get(SERIALISE_KEY_AUDIENCE) as? String ?: return null
            val subject = get(SERIALISE_KEY_SUBJECT) as? String ?: return null
            val (issuedAt, expiredAt) = extractTimestamps() ?: return null

            return Impl(
                certificateId = certificateId,
                issuer = issuer,
                audience = audience,
                subject = subject,
                issuedAt = issuedAt,
                expiredAt = expiredAt
            )
        }

        internal fun Map<String, Any>.extractTimestamps(): Pair<Instant, Instant>? {
            val issuedAt = (get(SERIALISE_KEY_ISSUED_AT) as? Number)?.toLong()?.let {
                Instant.ofEpochSecond(it)
            }
            val expiredAt = (get(SERIALISE_KEY_EXPIRED_AT) as? Number)?.toLong()?.let {
                Instant.ofEpochSecond(it)
            }

            return if (issuedAt == null || expiredAt == null) {
                null
            } else {
                issuedAt to expiredAt
            }
        }

        private data class Impl(
            override val certificateId: String,
            override val issuer: String,
            override val audience: String,
            override val subject: String,
            override val issuedAt: Instant,
            override val expiredAt: Instant
        ) : AuthenticationTokenPayload
    }
}
