/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.model.auth

import com.sirloin.jvmlib.net.isIpV4Address
import net.meatplatform.sandbox.util.IpAddressValidatorMixin
import java.net.InetAddress
import java.time.Instant

/**
 * @since 2022-12-27
 */
interface AccessTokenPayload : AuthenticationTokenPayload, IpAddressValidatorMixin {
    val ipAddress: InetAddress

    override fun serialise(): Map<String, Any> = HashMap(super.serialise()).apply {
        put(SERIALISE_KEY_IPV4, ipAddress.toString())
    }

    fun isAcceptableIpAddress(currentIpAddress: InetAddress): Boolean =
        ipAddress.isAcceptableIpAddress(currentIpAddress)

    companion object {
        const val SERIALISE_KEY_IPV4 = "ipv4"

        fun create(
            certificate: RsaCertificate,
            issuer: String,
            audience: String,
            subject: String,
            issuedAt: Instant,
            expiredAt: Instant,
            ipAddress: InetAddress
        ): AccessTokenPayload {
            if (!ipAddress.isIpV4Address()) {
                throw IllegalArgumentException("Remote user does not support any IPv4 address.")
            }

            return AccessTokenPayloadImpl(
                certificate = certificate,
                issuer = issuer,
                audience = audience,
                subject = subject,
                issuedAt = issuedAt,
                expiredAt = expiredAt,
                ipAddress = ipAddress
            )
        }
    }
}

internal data class AccessTokenPayloadImpl(
    override val certificate: RsaCertificate,
    override val issuer: String,
    override val audience: String,
    override val subject: String,
    override val issuedAt: Instant,
    override val expiredAt: Instant,
    override val ipAddress: InetAddress
) : AccessTokenPayload
