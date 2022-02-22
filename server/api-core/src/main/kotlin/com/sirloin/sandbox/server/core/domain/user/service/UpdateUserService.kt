/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.domain.user.service

import com.sirloin.jvmlib.time.truncateToSeconds
import com.sirloin.sandbox.server.core.domain.user.User
import com.sirloin.sandbox.server.core.domain.user.common.UserServiceMixin
import com.sirloin.sandbox.server.core.domain.user.repository.UserRepository
import com.sirloin.sandbox.server.core.i18n.LocaleProvider
import java.time.Instant
import java.util.*

/**
 * User 도메인 모델 수정 관련 로직을 담당하는 Service interface
 *
 * @since 2022-02-14
 */
interface UpdateUserService {
    fun updateUser(
        uuid: UUID,
        nickname: String? = null,
        profileImageUrl: String? = null
    ): User

    companion object {
        fun newInstance(
            userRepo: UserRepository,
            localeProvider: LocaleProvider
        ): UpdateUserService = UpdateUserServiceImpl(userRepo, localeProvider)
    }
}

internal class UpdateUserServiceImpl(
    override val userRepo: UserRepository,
    override val localeProvider: LocaleProvider
) : UpdateUserService, UserServiceMixin {
    override fun updateUser(uuid: UUID, nickname: String?, profileImageUrl: String?): User {
        val user = super.getUserByUuid(uuid)

        return userRepo.save(user.edit().apply {
            this.nickname = nickname ?: user.nickname
            this.profileImageUrl = profileImageUrl ?: user.profileImageUrl
            this.updatedAt = Instant.now().truncateToSeconds()
        })
    }
}
