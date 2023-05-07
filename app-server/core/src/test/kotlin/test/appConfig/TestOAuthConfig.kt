/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.appConfig

import net.meatplatform.sandbox.appconfig.OAuthConfig

/**
 * @since 2023-05-07
 */
class TestOAuthConfig : OAuthConfig {
    override val googleOAuthUrl: String = "https://accounts.google.com/o/oauth2/v2/auth"

    override val appleOAuthUrl: String = "https://appleid.apple.com/auth/keys"
}
