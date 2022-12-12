/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.ComponentScan

/**
 * @since 2022-02-14
 */
fun main(args: Array<String>) {
    // Loads application.yml first, and overrides settings, declared if any, in application-<BUILD_PHASE>.yml.
    val configurationNames = arrayOf("application", "application-${SandboxApplication.buildConfig.profile.code}")

    SandboxApplication().start(args, configurationNames)
}

@SpringBootApplication
@ComponentScan(
    basePackages = [
        SandboxApplication.APP_PACKAGE_NAME,
        SandboxApplication.INFRA_PACKAGE_NAME,
    ]
)
class SandboxApplication {
    fun start(args: Array<String>, configurationNames: Array<String>) {
        // 한번만 호출하니까, Performance 경고를 무시합니다.
        @Suppress("SpreadOperator")
        SpringApplicationBuilder(SandboxApplication::class.java)
            .properties("spring.config.name:" + configurationNames.joinToString { it })
            .build()
            .run(*args)
    }

    companion object {
        const val APP_PACKAGE_NAME = "net.meatplatform.sandbox"
        const val INFRA_PACKAGE_NAME = "net.meatplatform.sandbox"
        const val DEFAULT_APP_VERSION = "1.0.0"

        private const val BUILD_CONFIG_FILE = "build_meatplatform-sandbox-server.json"
        private const val KEY_VERSION = "version"
        private const val KEY_FINGERPRINT = "fingerprint"
        private const val KEY_PROFILE = "profile"

        private const val DEFAULT_FINGERPRINT = "UNSPECIFIED"
        private val DEFAULT_APP_PROFILE = AppProfile.LOCAL

        val buildConfig: BuildConfig by lazy {
            val resource = SandboxApplication::class.java.classLoader.getResourceAsStream(BUILD_CONFIG_FILE)
                ?: return@lazy BuildConfigImpl(
                    version = DEFAULT_APP_VERSION,
                    fingerprint = DEFAULT_FINGERPRINT,
                    profile = DEFAULT_APP_PROFILE
                )

            val nodes = ObjectMapper().readTree(resource)

            return@lazy BuildConfigImpl(
                version = nodes.get(KEY_VERSION)?.asText() ?: DEFAULT_APP_VERSION,
                fingerprint = nodes.get(KEY_FINGERPRINT)?.asText() ?: DEFAULT_FINGERPRINT,
                profile = AppProfile.from(nodes.get(KEY_PROFILE)?.asText()) ?: DEFAULT_APP_PROFILE
            )
        }

        interface BuildConfig {
            val version: String

            val fingerprint: String

            val profile: AppProfile
        }

        private data class BuildConfigImpl(
            override val version: String,
            override val fingerprint: String,
            override val profile: AppProfile,
        ) : BuildConfig
    }
}
