/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.appconfig.domain.user

import com.sirloin.sandbox.server.core.domain.user.repository.UserReadonlyRepository
import com.sirloin.sandbox.server.core.domain.user.repository.UserRepository
import com.sirloin.sandbox.server.core.domain.user.service.CreateUserService
import com.sirloin.sandbox.server.core.domain.user.service.DeleteUserService
import com.sirloin.sandbox.server.core.domain.user.service.GetUserService
import com.sirloin.sandbox.server.core.domain.user.service.UpdateUserService
import com.sirloin.sandbox.server.core.i18n.LocaleProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * User 도메인 로직을 Spring Bean 으로 등록합니다.
 *
 * @since 2022-02-14
 */
@Configuration
class UserBeanConfig {
    @Bean
    fun createUserService(
        @Qualifier(UserRepository.NAME) userRepository: UserRepository,
        localeProvider: LocaleProvider
    ): CreateUserService =
        CreateUserService.newInstance(userRepository, localeProvider)

    @Bean
    fun getUserService(
        @Qualifier(UserReadonlyRepository.NAME) userReadonlyRepository: UserReadonlyRepository,
        localeProvider: LocaleProvider
    ): GetUserService =
        GetUserService.newInstance(userReadonlyRepository, localeProvider)

    @Bean
    fun updateUserService(
        @Qualifier(UserRepository.NAME) userRepository: UserRepository,
        localeProvider: LocaleProvider
    ): UpdateUserService =
        UpdateUserService.newInstance(userRepository, localeProvider)

    @Bean
    fun deleteUserService(
        @Qualifier(UserRepository.NAME) userRepository: UserRepository,
        localeProvider: LocaleProvider
    ): DeleteUserService =
        DeleteUserService.newInstance(userRepository, localeProvider)
}
