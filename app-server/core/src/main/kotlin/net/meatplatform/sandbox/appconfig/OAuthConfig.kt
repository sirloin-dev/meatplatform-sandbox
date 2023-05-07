/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.appconfig

/**
 * @since 2023-05-07
 */
interface OAuthConfig {
    val googleOAuthUrl: String

    val appleOAuthUrl: String
}
