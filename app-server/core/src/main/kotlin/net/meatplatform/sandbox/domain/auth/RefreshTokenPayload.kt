/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.auth

import net.meatplatform.sandbox.domain.auth.AuthenticationTokenPayload.Companion.SERIALISE_KEY_AUDIENCE
import net.meatplatform.sandbox.domain.auth.AuthenticationTokenPayload.Companion.SERIALISE_KEY_ISSUED_AT
import net.meatplatform.sandbox.domain.auth.AuthenticationTokenPayload.Companion.SERIALISE_KEY_ISSUER
import net.meatplatform.sandbox.domain.auth.AuthenticationTokenPayload.Companion.SERIALISE_KEY_KEY_ID
import net.meatplatform.sandbox.domain.auth.AuthenticationTokenPayload.Companion.SERIALISE_KEY_SUBJECT
import net.meatplatform.sandbox.domain.auth.mutator.RefreshTokenPayloadMutator
import java.time.Instant

/**
 * @since 2022-12-27
 */
interface RefreshTokenPayload : AuthenticationTokenPayload {
    companion object {
        fun create(
            certificateId: String,
            issuer: String,
            audience: String,
            subject: String,
            issuedAt: Instant,
            expiredAt: Instant
        ): RefreshTokenPayload = RefreshTokenPayloadMutator(
            certificateId = certificateId,
            issuer = issuer,
            audience = audience,
            subject = subject,
            issuedAt = issuedAt,
            expiredAt = expiredAt
        )

        @Suppress("ReturnCount")    // Early return 으로 불필요한 가정을 제거해 가독성을 높인다.
        fun deserialise(serialisedMap: Map<String, Any>): RefreshTokenPayload? {
            with(serialisedMap) {
                val certificatedId = get(SERIALISE_KEY_KEY_ID) as? String ?: return null
                val issuer = get(SERIALISE_KEY_ISSUER) as? String ?: return null
                val audience = get(SERIALISE_KEY_AUDIENCE) as? String ?: return null
                val subject = get(SERIALISE_KEY_SUBJECT) as? String ?: return null
                val issuedAt = (get(SERIALISE_KEY_ISSUED_AT) as? Number)?.toLong()?.let {
                    Instant.ofEpochSecond(it)
                } ?: return null
                val expiredAt = (get(SERIALISE_KEY_ISSUED_AT) as? Number)?.toLong()?.let {
                    Instant.ofEpochSecond(it)
                } ?: return null

                return create(
                    certificateId = certificatedId,
                    issuer = issuer,
                    audience = audience,
                    subject = subject,
                    issuedAt = issuedAt,
                    expiredAt = expiredAt
                )
            }
        }
    }
}
