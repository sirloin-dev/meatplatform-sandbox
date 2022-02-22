/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.domain.user.repository

import com.sirloin.sandbox.server.core.domain.user.User

/**
 * User 도메인 모델의 읽기 및 쓰기 가능 저장소 인터페이스
 *
 * @since 2022-02-14
 */
interface UserRepository : UserReadonlyRepository {
    fun save(user: User): User

    companion object {
        const val NAME = "UserRepository"
    }
}
