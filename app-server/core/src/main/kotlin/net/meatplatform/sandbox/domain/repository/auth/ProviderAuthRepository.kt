/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.repository.auth

import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication

/**
 * @since 2022-02-14
 */
interface ProviderAuthRepository {
    fun verifyProviderAuth(type: ProviderAuthentication.Type, providerAuthToken: String): ProviderAuthentication

    fun findByIdentity(type: ProviderAuthentication.Type, providerId: String): ProviderAuthentication?

    companion object {
        const val NAME = "net.meatplatform.sandbox.domain.repository.auth.ProviderAuthRepository"
    }
}
