/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.model.auth

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
        ): RefreshTokenPayload = RefreshTokenPayloadImpl(
            certificate = certificate,
            issuer = issuer,
            audience = audience,
            subject = subject,
            issuedAt = issuedAt,
            expiredAt = expiredAt
        )
    }
}

internal data class RefreshTokenPayloadImpl(
    override val certificate: RsaCertificate,
    override val issuer: String,
    override val audience: String,
    override val subject: String,
    override val issuedAt: Instant,
    override val expiredAt: Instant
) : RefreshTokenPayload
