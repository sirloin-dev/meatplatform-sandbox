/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.domain.user.service

import com.sirloin.sandbox.server.core.domain.user.User
import com.sirloin.sandbox.server.core.domain.user.common.UserServiceMixin
import com.sirloin.sandbox.server.core.domain.user.repository.UserRepository
import com.sirloin.sandbox.server.core.i18n.LocaleProvider
import java.util.*

/**
 * User 도메인 모델 제거 관련 로직을 담당하는 Service interface
 *
 * @since 2022-02-14
 */
interface DeleteUserService {
    fun deleteUserByUuid(uuid: UUID): User

    companion object {
        fun newInstance(
            userRepo: UserRepository,
            localeProvider: LocaleProvider
        ): DeleteUserService = DeleteUserServiceImpl(userRepo, localeProvider)
    }
}

class DeleteUserServiceImpl(
    override val userRepo: UserRepository,
    override val localeProvider: LocaleProvider
) : DeleteUserService, UserServiceMixin {
    override fun deleteUserByUuid(uuid: UUID): User {
        val user = super.getUserByUuid(uuid)

        return userRepo.save(user.edit().apply {
            this.delete()
        })
    }
}
