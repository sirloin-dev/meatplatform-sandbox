/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.appconfig.domain.usecase

import net.meatplatform.sandbox.domain.usecase.auth.CreateAccessTokenUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @since 2022-12-26
 */
@Configuration
internal class AuthBeansConfig {
    @Bean
    fun createAccessTokenUseCase() = CreateAccessTokenUseCase.newInstance()
}
