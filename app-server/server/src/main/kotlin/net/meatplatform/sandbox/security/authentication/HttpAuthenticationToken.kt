/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.security.authentication

import org.springframework.security.authentication.AbstractAuthenticationToken
import java.time.Instant

/**
 * @since 2022-03-20
 */
data class HttpAuthenticationToken(
    private val scheme: HttpAuthenticationScheme,
    private val token: String
) : AbstractAuthenticationToken(null) {
    override fun getPrincipal(): HttpAuthenticationScheme = scheme

    override fun getCredentials(): String = token

    companion object {
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Authorization
        const val HEADER_NAME = "Authorization"
    }
}
