/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.auth.repository

import net.meatplatform.sandbox.domain.auth.ProviderAuthentication

/**
 * @since 2022-02-14
 */
interface ProviderAuthRepository {
    fun verifyProviderAuth(type: ProviderAuthentication.Type, providerAuthToken: String): ProviderAuthentication

    fun findByEmailAuthIdentity(email: String, password: String): ProviderAuthentication?

    fun findByProviderAuthIdentity(type: ProviderAuthentication.Type, providerId: String): ProviderAuthentication?

    companion object {
        const val NAME = "net.meatplatform.sandbox.domain.auth.ProviderAuthRepository"
    }
}
