/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.user.repository

import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.user.User
import java.util.*

/**
 * @since 2022-02-14
 */
interface UserRepository {
    fun findById(id: UUID): User?

    fun findByProviderAuth(providerAuth: ProviderAuthentication): User?

    fun save(user: User): User

    companion object {
        const val NAME = "net.meatplatform.sandbox.domain.user.UserRepository"
    }
}
