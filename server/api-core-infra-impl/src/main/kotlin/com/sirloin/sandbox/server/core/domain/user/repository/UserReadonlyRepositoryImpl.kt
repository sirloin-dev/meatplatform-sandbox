/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.domain.user.repository

import com.sirloin.sandbox.server.core.domain.user.User
import com.sirloin.sandbox.server.core.domain.user.UserEntity
import com.sirloin.sandbox.server.core.domain.user.repository.jdbc.UserEntityDao
import org.springframework.stereotype.Component
import java.util.*

/**
 * UserReadonlyRepository 구현체입니다.
 * Repository 는 여러 DAO 의 조합으로 구성할 수 있습니다.
 * 각 DAO 들은 다양한 data source 에 접근해 domain model 을 구성하도록 동작합니다.
 *
 * 가령 RDBMS + NoSQL 조합으로 Domain model 을 구성할 수도 있고,
 * Data source + Cache 조합으로 조회 성능을 끌어올리는 등의 구현도 가능합니다.
 *
 * @since 2022-02-14
 */
@Component(UserReadonlyRepository.NAME)
internal class UserReadonlyRepositoryImpl(
    private val userDao: UserEntityDao
) : UserReadonlyRepository {
    fun findById(id: Long): UserEntity {
        return userDao.findById(id).get()
    }

    override fun findByUuid(uuid: UUID): User? {
        return userDao.findByUuid(uuid)
    }
}
