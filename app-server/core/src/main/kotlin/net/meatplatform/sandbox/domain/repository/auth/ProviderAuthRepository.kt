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
    fun verify(type: ProviderAuthentication.Type, providerAuthToken: String): ProviderAuthentication
}
