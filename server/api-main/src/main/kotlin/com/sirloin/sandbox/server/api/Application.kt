package com.sirloin.sandbox.server.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.sirloin.sandbox.server.core.CoreApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.ComponentScan

fun main(args: Array<String>) {
    // Loads application.yml first, and overrides settings, declared if any, in application-<BUILD_PHASE>.yml.
    val configurationNames = arrayOf("application", "application-" + Application.buildConfig.profile.profileName)

    Application().start(args, configurationNames)
}

/*
 * POINT: 이 Application 은 무슨 일을 하나요? 1문장으로, 3분에, 10분에 걸쳐서 설명해 주세요.
 */
@SpringBootApplication
@ComponentScan(basePackages = [CoreApplication.PACKAGE_NAME, Application.PACKAGE_NAME])
class Application {
    fun start(args: Array<String>, configurationNames: Array<String>) {
        // This logic is called only once.
        @Suppress("SpreadOperator")
        SpringApplicationBuilder(Application::class.java)
            .properties("spring.config.name:" + configurationNames.joinToString { it })
            .build()
            .run(*args)
    }

    companion object {
        const val PACKAGE_NAME = "com.sirloin.sandbox.server.api"

        private const val BUILD_CONFIG_FILE = "build_api-main.json"
        private const val KEY_VERSION = "version"
        private const val KEY_FINGERPRINT = "fingerprint"
        private const val KEY_PROFILE = "profile"

        private const val DEFAULT_VERSION = "0.0.1"
        private const val DEFAULT_FINGERPRINT = "UNSPECIFIED"
        private val DEFAULT_APP_PROFILE = AppProfile.LOCAL

        val buildConfig: BuildConfig by lazy {
            val resource = Application::class.java.classLoader.getResourceAsStream(BUILD_CONFIG_FILE)
                ?: return@lazy BuildConfigImpl(
                    version = DEFAULT_VERSION,
                    fingerprint = DEFAULT_FINGERPRINT,
                    profile = DEFAULT_APP_PROFILE
                )

            val nodes = ObjectMapper().readTree(resource)

            return@lazy BuildConfigImpl(
                version = nodes.get(KEY_VERSION)?.asText() ?: DEFAULT_VERSION,
                fingerprint = nodes.get(KEY_FINGERPRINT)?.asText() ?: DEFAULT_FINGERPRINT,
                profile = AppProfile.from(nodes.get(KEY_PROFILE)?.asText(), DEFAULT_APP_PROFILE)
            )
        }
    }

    interface BuildConfig {
        val version: String

        val fingerprint: String

        val profile: AppProfile
    }

    private data class BuildConfigImpl(
        override val version: String,
        override val fingerprint: String,
        override val profile: AppProfile
    ) : BuildConfig
}
