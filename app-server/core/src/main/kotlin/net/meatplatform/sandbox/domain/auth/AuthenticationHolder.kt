/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.auth

import java.net.InetAddress
import java.time.Instant
import java.util.*

/**
 * @since 2023-05-09
 */
interface AuthenticationHolder {
    val userId: UUID

    val authIssuer: String

    val authAudience: String

    val authIpAddress: InetAddress

    val issuedAt: Instant

    val expiredAt: Instant

    val clientInfo: ClientInfo?
}
