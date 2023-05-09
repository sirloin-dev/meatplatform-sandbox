/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.auth

import com.sirloin.jvmlib.net.isIpV4Address
import net.meatplatform.sandbox.domain.auth.AuthenticationTokenPayload.Companion.SERIALISE_KEY_AUDIENCE
import net.meatplatform.sandbox.domain.auth.AuthenticationTokenPayload.Companion.SERIALISE_KEY_ISSUER
import net.meatplatform.sandbox.domain.auth.AuthenticationTokenPayload.Companion.SERIALISE_KEY_KEY_ID
import net.meatplatform.sandbox.domain.auth.AuthenticationTokenPayload.Companion.SERIALISE_KEY_SUBJECT
import net.meatplatform.sandbox.domain.auth.AuthenticationTokenPayload.Companion.extractTimestamps
import net.meatplatform.sandbox.domain.auth.mutator.AccessTokenPayloadMutator
import java.net.InetAddress
import java.time.Instant

/**
 * @since 2022-12-27
 */
interface AccessTokenPayload : AuthenticationTokenPayload {
    val ipAddress: InetAddress

    override fun serialise(): Map<String, Any> = HashMap(super.serialise()).apply {
        put(SERIALISE_KEY_IPV4, ipAddress.hostAddress)
    }

    companion object {
        const val SERIALISE_KEY_IPV4 = "ipv4"

        fun create(
            certificateId: String,
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

            return AccessTokenPayloadMutator(
                certificateId = certificateId,
                issuer = issuer,
                audience = audience,
                subject = subject,
                issuedAt = issuedAt,
                expiredAt = expiredAt,
                ipAddress = ipAddress
            )
        }

        fun deserialise(serialisedMap: Map<String, Any>): AccessTokenPayload? = with(AuthenticationTokenPayload) {
            val authTokenPayload = serialisedMap.parse()
            val ipAddress = (serialisedMap[SERIALISE_KEY_IPV4] as? String)?.let { InetAddress.getByName(it) }

            return if (authTokenPayload != null && ipAddress != null) {
                authTokenPayload.run {
                    create(
                        certificateId = certificateId,
                        issuer = issuer,
                        audience = audience,
                        subject = subject,
                        issuedAt = issuedAt,
                        expiredAt = expiredAt,
                        ipAddress = ipAddress
                    )
                }
            } else {
                null
            }
        }
    }
}
