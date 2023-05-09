/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.auth.mutator

import net.meatplatform.sandbox.domain.auth.RefreshTokenPayload
import net.meatplatform.sandbox.domain.auth.RsaCertificate
import java.time.Instant

/**
 * @since 2023-05-06
 */
data class RefreshTokenPayloadMutator(
    override val certificateId: String,
    override val issuer: String,
    override val audience: String,
    override val subject: String,
    override val issuedAt: Instant,
    override val expiredAt: Instant
) : RefreshTokenPayload
