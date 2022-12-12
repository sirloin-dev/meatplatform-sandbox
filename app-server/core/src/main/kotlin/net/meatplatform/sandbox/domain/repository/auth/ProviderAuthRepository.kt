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
    /**
     * [ProviderAuthentication.Type.GOOGLE], [ProviderAuthentication.Type.APPLE] 소셜 인증을 검증합니다.
     */
    fun verifyProviderAuth(type: ProviderAuthentication.Type, providerAuthToken: String): ProviderAuthentication

    fun findByIdentity(type: ProviderAuthentication.Type, providerId: String): ProviderAuthentication?

    companion object {
        const val NAME = "domain.repository.auth.ProviderAuthRepository"
    }
}
