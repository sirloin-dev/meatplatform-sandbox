/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.appconfig.domain.usecase

import net.meatplatform.sandbox.domain.repository.auth.ProviderAuthRepository
import net.meatplatform.sandbox.domain.repository.user.UserRepository
import net.meatplatform.sandbox.domain.usecase.user.CreateUserUseCase
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @since 2022-02-14
 */
@Configuration
internal class UserBeansConfig {
    @Bean
    fun createUserUseCase(
        @Qualifier(ProviderAuthRepository.NAME)
        providerAuthRepository: ProviderAuthRepository,
        userRepository: UserRepository
    ) = CreateUserUseCase.newInstance(
        providerAuthRepository = providerAuthRepository,
        userRepository = userRepository
    )
}