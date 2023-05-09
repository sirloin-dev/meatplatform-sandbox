/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.appconfig.domain.usecase

import net.meatplatform.sandbox.appconfig.security.AuthenticationPolicy
import net.meatplatform.sandbox.domain.auth.repository.ProviderAuthRepository
import net.meatplatform.sandbox.domain.auth.repository.RsaCertificateRepository
import net.meatplatform.sandbox.domain.auth.usecase.CreateAccessTokenUseCase
import net.meatplatform.sandbox.domain.auth.usecase.LoginUseCase
import net.meatplatform.sandbox.domain.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @since 2022-12-26
 */
@Configuration
internal class AuthBeansConfig {
    @Bean
    fun createAccessTokenUseCase(
        policy: AuthenticationPolicy,
        @Qualifier(RsaCertificateRepository.NAME) rsaCertificateRepository: RsaCertificateRepository
    ) = CreateAccessTokenUseCase.newInstance(
        issuerName = policy.issuer,
        accessTokenLifeSeconds = policy.accessTokenLifeInSeconds,
        refreshTokenLifeSeconds = policy.refreshTokenLifeInSeconds,
        rsaCertificateRepository = rsaCertificateRepository
    )

    @Bean
    fun loginUseCase(
        @Qualifier(ProviderAuthRepository.NAME) providerAuthRepository: ProviderAuthRepository,
        userRepository: UserRepository
    ) = LoginUseCase.newInstance(
        providerAuthRepository = providerAuthRepository,
        userRepository = userRepository
    )
}
