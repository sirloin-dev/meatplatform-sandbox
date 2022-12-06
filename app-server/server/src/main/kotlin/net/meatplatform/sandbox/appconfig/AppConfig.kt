/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.appconfig

import net.meatplatform.sandbox.SandboxApplication
import net.meatplatform.sandbox.SandboxApplication.Companion.BuildConfig
import org.slf4j.Logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Application 전체에 활용할 환경 설정 처리 로직
 *
 * @since 2022-02-14
 */
@Configuration
internal class AppConfig(
    private val log: Logger,
) {
    @Bean
    fun buildConfig(): BuildConfig {
        return SandboxApplication.buildConfig.also {
            this.log.info("Build configurations - ")
            this.log.info("  Version:     {}", it.version)
            this.log.info("  Fingerprint: {}", it.fingerprint)
            this.log.info("  Profile:     {}", it.profile)
        }
    }
}
