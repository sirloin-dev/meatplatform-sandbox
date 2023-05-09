/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.security.authentication

import net.meatplatform.sandbox.domain.auth.AccessTokenPayload
import net.meatplatform.sandbox.domain.auth.AuthenticationHolder
import net.meatplatform.sandbox.domain.auth.AuthenticationTokenPayload
import net.meatplatform.sandbox.domain.auth.ClientInfo
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import java.net.InetAddress
import java.time.Instant
import java.util.*

/**
 * @since 2022-03-20
 */
data class VerifiedAuthentication(
    override val userId: UUID,
    override val authIssuer: String,
    override val authAudience: String,
    override val authIpAddress: InetAddress?,
    override val issuedAt: Instant,
    override val expiredAt: Instant,
    override val clientInfo: ClientInfo?
) : Authentication, AuthenticationHolder {
    private val authorities = ArrayList<GrantedAuthority>()

    // User Name 을 모르므로 일단은 empty string 처리한다.
    override fun getName(): String = ""

    // Unsupported 로 처리한 로직은 추후 필요하다면 lazy load 가능하도록 구현할 것.
    override fun getAuthorities(): MutableCollection<GrantedAuthority> = authorities

    override fun getCredentials(): Any = throw UnsupportedOperationException("getCredentials() is not supported")

    override fun getDetails(): Any = throw UnsupportedOperationException("getDetails() is not supported")

    override fun getPrincipal(): Any = userId

    override fun isAuthenticated(): Boolean = true

    override fun setAuthenticated(isAuthenticated: Boolean) {
        if (!isAuthenticated) {
            throw IllegalArgumentException("Cannot set isAuthenticated flag to false")
        }
    }

    companion object {
        fun from(authenticationTokenPayload: AuthenticationTokenPayload): VerifiedAuthentication =
            authenticationTokenPayload.run {
                VerifiedAuthentication(
                    userId = UUID.fromString(subject),
                    authIssuer = issuer,
                    authAudience = audience,
                    authIpAddress = if (this is AccessTokenPayload) {
                        ipAddress
                    } else {
                        null
                    },
                    issuedAt = issuedAt,
                    expiredAt = expiredAt,
                    // 필요한 곳에서 re-assign 할 것.
                    clientInfo = null
                )
            }
    }
}
