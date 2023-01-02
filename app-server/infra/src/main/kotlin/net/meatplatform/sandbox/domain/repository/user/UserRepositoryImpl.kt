/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.repository.user

import net.meatplatform.sandbox.annotation.InfrastructureService
import net.meatplatform.sandbox.domain.model.user.User
import net.meatplatform.sandbox.jpa.dao.read.UserEntityReadDao
import net.meatplatform.sandbox.jpa.dao.write.UserEntityWriteDao
import net.meatplatform.sandbox.jpa.entity.user.UserEntity
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * @since 2022-02-14
 */
@InfrastructureService
internal class UserRepositoryImpl(
    private val userReader: UserEntityReadDao,
    private val userWriter: UserEntityWriteDao
) : UserRepository {
    @Transactional
    override fun findById(id: UUID): User? = userReader.findById(id)?.let { User.fromEntity(it) }

    @Transactional
    override fun save(user: User): User {
        val userEntity = user.run {
            if (isIdentifiable) {
                userReader.findById(id)?.importValues(this) ?: toEntity()
            } else {
                toEntity()
            }
        }

        return saveInternal(userEntity)
    }

    private fun saveInternal(userEntity: UserEntity): User {
        val savedEntity = userWriter.save(userEntity)
        return User.fromEntity(savedEntity)
    }
}
