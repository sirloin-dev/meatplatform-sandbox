/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.repository.user

import net.meatplatform.sandbox.domain.model.user.User

/**
 * @since 2022-02-14
 */
interface UserRepository {
    fun save(user: User): User
}
