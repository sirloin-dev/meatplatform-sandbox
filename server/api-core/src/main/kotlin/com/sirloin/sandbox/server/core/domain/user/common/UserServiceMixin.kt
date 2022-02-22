/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.domain.user.common

import com.sirloin.sandbox.server.core.domain.user.User
import com.sirloin.sandbox.server.core.domain.user.exception.UserNotFoundException
import com.sirloin.sandbox.server.core.domain.user.repository.UserReadonlyRepository
import com.sirloin.sandbox.server.core.i18n.LocaleProvider
import java.util.*

/**
 * User 도메인 모델 처리에 필요한 공통 로직 모음
 *
 * @since 2022-02-14
 */
// POINT: Mixin 이란 무엇일까요? 왜 추상 클래스가 아니라 Interface 로 구현했을까요?
internal interface UserServiceMixin {
    val userRepo: UserReadonlyRepository
    val localeProvider: LocaleProvider

    fun getUserByUuid(uuid: UUID): User {
        return userRepo.findByUuid(uuid) ?: throw UserNotFoundException(localeProvider, uuid)
    }
}
