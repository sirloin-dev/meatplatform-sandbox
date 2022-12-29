/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.appconfig.domain.usecase

import net.meatplatform.sandbox.domain.repository.auth.RsaCertificateRepository
import net.meatplatform.sandbox.domain.usecase.auth.CreateAccessTokenUseCase
import net.meatplatform.sandbox.exception.internal.IllegalConfigValueException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @since 2022-12-26
 */
@Configuration
internal class AuthBeansConfig {
    @Bean
    fun createAccessTokenUseCase(
        @Value("\${$CONFIG_AUTH_TOKEN_ISSUER}") issuerName: String,
        @Value("\${$CONFIG_AUTH_TOKEN_ACCESS_TOKEN_LIFE_SECONDS}") accessTokenLifeSeconds: String,
        @Value("\${$CONFIG_AUTH_TOKEN_REFRESH_TOKEN_LIFE_SECONDS}") refreshTokenLifeSeconds: String,
        @Qualifier(RsaCertificateRepository.NAME)
        rsaCertificateRepository: RsaCertificateRepository
    ) = CreateAccessTokenUseCase.newInstance(
        issuerName = issuerName,
        accessTokenLifeSeconds = accessTokenLifeSeconds.toLongOrNull() ?: throw IllegalConfigValueException(
            CONFIG_AUTH_TOKEN_ACCESS_TOKEN_LIFE_SECONDS,
            accessTokenLifeSeconds,
            Long::class
        ),
        refreshTokenLifeSeconds = refreshTokenLifeSeconds.toLongOrNull() ?: throw IllegalConfigValueException(
            CONFIG_AUTH_TOKEN_REFRESH_TOKEN_LIFE_SECONDS,
            refreshTokenLifeSeconds,
            Long::class
        ),
        rsaCertificateRepository = rsaCertificateRepository
    )

    companion object {
        private const val CONFIG_AUTH_TOKEN = "sandboxapp.authentication.token"
        private const val CONFIG_AUTH_TOKEN_ISSUER = "$CONFIG_AUTH_TOKEN.issuer"
        private const val CONFIG_AUTH_TOKEN_ACCESS_TOKEN_LIFE_SECONDS = "$CONFIG_AUTH_TOKEN.accessTokenLifeSeconds"
        private const val CONFIG_AUTH_TOKEN_REFRESH_TOKEN_LIFE_SECONDS = "$CONFIG_AUTH_TOKEN.refreshTokenLifeSeconds"
    }
}
