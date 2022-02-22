/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.domain.user.repository

import com.sirloin.sandbox.server.core.domain.user.User
import java.util.*

/**
 * User 도메인 모델의 읽기 전용 저장소 인터페이스
 *
 * @since 2022-02-14
 */
interface UserReadonlyRepository {
    fun findByUuid(uuid: UUID): User?

    companion object {
        const val NAME = "UserReadonlyRepository"
    }
}
