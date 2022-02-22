/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.domain.user.service

import com.sirloin.sandbox.server.core.domain.user.User
import com.sirloin.sandbox.server.core.domain.user.repository.UserRepository
import com.sirloin.sandbox.server.core.i18n.LocaleProvider
import java.util.*

/**
 * User 도메인 모델 생성 관련 로직을 담당하는 Service interface
 *
 * @since 2022-02-14
 */
interface CreateUserService {
    fun createUser(
        nickname: String,
        profileImageUrl: String
    ): User

    companion object {
        fun newInstance(
            userRepo: UserRepository,
            localeProvider: LocaleProvider
        ): CreateUserService = CreateUserServiceImpl(userRepo, localeProvider)
    }
}

internal class CreateUserServiceImpl(
    private val userRepo: UserRepository,
    private val localeProvider: LocaleProvider
) : CreateUserService {
    override fun createUser(nickname: String, profileImageUrl: String): User {
        return userRepo.save(
            User.create(
                uuid = UUID.randomUUID(),
                nickname = nickname,
                profileImageUrl = profileImageUrl
            )
        )
    }
}
