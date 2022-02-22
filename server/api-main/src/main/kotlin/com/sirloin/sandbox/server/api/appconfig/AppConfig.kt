/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.appconfig

import com.sirloin.sandbox.server.api.Application
import org.slf4j.Logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Application 전체에 활용할 환경 설정 처리 로직
 *
 * @since 2022-02-14
 */
@Configuration
class AppConfig(
    private val log: Logger,
) {
    @Bean
    fun buildConfig(): Application.BuildConfig {
        return Application.buildConfig.also {
            this.log.info("Build configurations - ")
            this.log.info("  Version:     {}", it.version)
            this.log.info("  Fingerprint: {}", it.fingerprint)
            this.log.info("  Profile:     {}", it.profile)
        }
    }
}
