/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.repository.auth

import net.meatplatform.sandbox.annotation.InfrastructureService
import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication
import org.springframework.transaction.annotation.Transactional

/**
 * @since 2022-02-14
 */
@InfrastructureService
internal class ProviderAuthRepositoryImpl : ProviderAuthRepository {
    @Transactional
    override fun verifyProviderAuth(
        type: ProviderAuthentication.Type,
        providerAuthToken: String
    ): ProviderAuthentication {
        TODO("Not yet implemented")
    }

    @Transactional
    override fun findByIdentity(type: ProviderAuthentication.Type, providerId: String): ProviderAuthentication? {
        TODO("Not yet implemented")
    }
}
