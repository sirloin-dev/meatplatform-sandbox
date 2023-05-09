/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.appconfig.security

/**
 * @since 2022-03-20
 */
data class AuthenticationPolicy(
    val validationCacheCount: Int,
    val issuer: String,
    val audience: String,
    val accessTokenLifeInSeconds: Long,
    val refreshTokenLifeInSeconds: Long
)
