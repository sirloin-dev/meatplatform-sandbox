/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.appconfig

import com.fasterxml.jackson.databind.ObjectMapper
import net.meatplatform.sandbox.CoreApplication
import net.meatplatform.sandbox.util.ToStringHelper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @since 2023-05-07
 */
@Configuration
class CoreApplicationSetup(
    private val clientVersionConfigHolder: ClientVersionConfigHolder
) {
    @Bean
    fun coreApplication(
        toStringHelper: ToStringHelper,
        oAuthConfig: OAuthConfig
    ): CoreApplication {
        val instance = object : CoreApplication {
            override fun toStringHelper(): ToStringHelper = toStringHelper

            override fun clientVersionConfig(): ClientVersionConfigHolder = clientVersionConfigHolder

            override fun oAuthConfig(): OAuthConfig = oAuthConfig
        }

        return instance.also { CoreApplication.init(it) }
    }

    @Bean
    fun toStringHelper(objectMapper: ObjectMapper): ToStringHelper = object : ToStringHelper {
        override fun toString(value: Any?): String =
            if (value == null) {
                "null"
            } else {
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value)
            }
    }

    @Bean
    fun oAuthConfig(
        @Value("\${$CONFIG_3RDPARTY_GOOGLE_OAUTH_URL}") googleOAuthUrl: String,
        @Value("\${$CONFIG_3RDPARTY_APPLE_OAUTH_URL}") appleOAuthUrl: String
    ): OAuthConfig = object : OAuthConfig {
        override val googleOAuthUrl: String = googleOAuthUrl

        override val appleOAuthUrl: String = appleOAuthUrl
    }

    companion object {
        private const val CONFIG_3RDPARTY = "sandboxapp.thirdparty"
        private const val CONFIG_3RDPARTY_GOOGLE = "${CONFIG_3RDPARTY}.google"
        private const val CONFIG_3RDPARTY_GOOGLE_OAUTH_URL = "${CONFIG_3RDPARTY_GOOGLE}.oAuthUrl"
        private const val CONFIG_3RDPARTY_APPLE = "${CONFIG_3RDPARTY}.apple"
        private const val CONFIG_3RDPARTY_APPLE_OAUTH_URL = "${CONFIG_3RDPARTY_APPLE}.oAuthUrl"
    }
}
