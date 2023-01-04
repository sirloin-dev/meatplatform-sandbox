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
            certificate: RsaCertificate,
            issuer: String,
            audience: String,
            subject: String,
            issuedAt: Instant,
            expiredAt: Instant
        ): RefreshTokenPayload = RefreshTokenPayloadMutator(
            certificate = certificate,
            issuer = issuer,
            audience = audience,
            subject = subject,
            issuedAt = issuedAt,
            expiredAt = expiredAt
        )
    }
}
