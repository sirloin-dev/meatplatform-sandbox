/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.auth.mutator

import net.meatplatform.sandbox.domain.auth.RsaCertificate
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.util.*

/**
 * @since 2023-05-06
 */
data class RsaCertificateMutator(
    override val id: UUID,
    override val isEnabled: Boolean,
    override val keySize: Int,
    override val publicKey: RSAPublicKey,
    override val privateKey: RSAPrivateKey,
    override val issuedAt: Instant,
    override val activeUntil: Instant
) : RsaCertificate
