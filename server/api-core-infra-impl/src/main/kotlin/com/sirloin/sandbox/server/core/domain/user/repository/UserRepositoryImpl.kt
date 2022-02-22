/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.domain.user.repository

import com.sirloin.sandbox.server.core.domain.user.User
import com.sirloin.sandbox.server.core.domain.user.UserEntity
import com.sirloin.sandbox.server.core.domain.user.repository.jdbc.UserEntityDao
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * @since 2022-02-14
 * @see UserReadonlyRepositoryImpl
 */
@Component(UserRepository.NAME)
internal class UserRepositoryImpl(
    @Qualifier(UserReadonlyRepository.NAME) private val userReadonlyRepository: UserReadonlyRepository,
    private val userDao: UserEntityDao
) : UserReadonlyRepository by userReadonlyRepository, UserRepository {
    override fun save(user: User): User {
        return userDao.save(UserEntity.from(user))
    }
}
