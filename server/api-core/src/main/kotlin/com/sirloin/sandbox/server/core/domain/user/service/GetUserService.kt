/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.domain.user.service

import com.sirloin.sandbox.server.core.domain.user.User
import com.sirloin.sandbox.server.core.domain.user.common.UserServiceMixin
import com.sirloin.sandbox.server.core.domain.user.repository.UserReadonlyRepository
import com.sirloin.sandbox.server.core.i18n.LocaleProvider
import java.util.*

/**
 * User 도메인 모델 조회 관련 로직을 담당하는 Service interface
 *
 * @since 2022-02-14
 */
interface GetUserService {
    fun findUserByUuid(uuid: UUID): User?

    fun getUserByUuid(uuid: UUID): User

    companion object {
        fun newInstance(
            userRepo: UserReadonlyRepository,
            localeProvider: LocaleProvider
        ): GetUserService = GetUserServiceImpl(userRepo, localeProvider)
    }
}

internal class GetUserServiceImpl(
    override val userRepo: UserReadonlyRepository,
    override val localeProvider: LocaleProvider
) : GetUserService, UserServiceMixin {
    override fun findUserByUuid(uuid: UUID): User? {
        return userRepo.findByUuid(uuid)
    }

    override fun getUserByUuid(uuid: UUID): User =
        super.getUserByUuid(uuid)
}
