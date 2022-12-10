/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.dao.write

import net.meatplatform.sandbox.jpa.entity.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * @since 2022-12-10
 */
internal interface UserEntityWriteDao {
    fun save(userEntity: UserEntity): UserEntity
}

@Repository
internal interface WriteUserEntityJpaRepository : JpaRepository<UserEntity, UUID>

@Repository
internal class UserEntityWriteDaoImpl(
    private val delegate: WriteUserEntityJpaRepository
) : UserEntityWriteDao {
    override fun save(userEntity: UserEntity): UserEntity =
        delegate.save(userEntity)
}
