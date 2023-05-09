/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.endpoint.v1.auth

/**
 * @since 2023-05-09
 */
data class AuthenticationTokenResponse(
    val accessToken: String,
    val refreshToken: String
)
