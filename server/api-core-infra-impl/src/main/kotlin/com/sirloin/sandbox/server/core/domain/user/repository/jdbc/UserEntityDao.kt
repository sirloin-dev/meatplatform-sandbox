/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.domain.user.repository.jdbc

import com.sirloin.sandbox.server.core.domain.user.UserEntity
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * User Entity 를 입출력하는 Data Access Object.
 *
 * @since 2022-02-14
 */
@Repository
internal interface UserEntityDao : CrudRepository<UserEntity, Long> {
    @Query("""
        SELECT *
        FROM `users` u
        WHERE u.`uuid` = :uuid
          AND u.`deleted_at` IS NULL
    """)
    fun findByUuid(@Param("uuid") uuid: UUID): UserEntity?
}
