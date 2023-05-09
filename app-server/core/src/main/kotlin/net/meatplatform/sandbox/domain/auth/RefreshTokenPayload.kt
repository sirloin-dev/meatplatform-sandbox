/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.auth

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

        fun deserialise(serialisedMap: Map<String, Any>): RefreshTokenPayload? = with(AuthenticationTokenPayload) {
            val authTokenPayload = serialisedMap.parse()

            return authTokenPayload?.run {
                create(
                    certificateId = certificateId,
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
