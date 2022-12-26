/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1.auth

import org.springframework.http.HttpHeaders

/**
 * @since 2022-12-09
 */
interface IssueTokenController {
    companion object {
        const val HEADER_AUTHORIZATION = HttpHeaders.AUTHORIZATION
        const val HEADER_X_AUTHORIZATION_RESPONSE = "X-Authorization-RefreshToken"
    }
}
