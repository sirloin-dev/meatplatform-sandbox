/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.repository.user

import net.meatplatform.sandbox.annotation.InfrastructureService
import net.meatplatform.sandbox.domain.model.user.User

/**
 * @since 2022-02-14
 */
@InfrastructureService
internal class UserRepositoryImpl : UserRepository {
    override fun save(user: User): User {
        TODO("Not yet implemented")
    }
}
