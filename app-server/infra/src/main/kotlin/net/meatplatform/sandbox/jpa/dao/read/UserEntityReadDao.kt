/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.dao.read

import net.meatplatform.sandbox.jpa.entity.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * @since 2022-12-10
 */
internal interface UserEntityReadDao {
    fun findById(id: UUID): UserEntity?
}

@Repository
internal interface ReadUserEntityJpaRepository : JpaRepository<UserEntity, UUID>

@Repository
internal class UserEntityReadDaoImpl(
    private val delegate: ReadUserEntityJpaRepository
) : UserEntityReadDao {
    override fun findById(id: UUID): UserEntity? =
        delegate.findById(id).orElse(null)
}
