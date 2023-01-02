/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.repository.auth

import net.meatplatform.sandbox.annotation.InfrastructureService
import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication.Type.*
import org.springframework.transaction.annotation.Transactional

/**
 * @since 2022-02-14
 */
@InfrastructureService(ProviderAuthRepository.NAME)
internal class ProviderAuthRepositoryImpl : ProviderAuthRepository {
    @Transactional
    override fun verifyProviderAuth(
        type: ProviderAuthentication.Type,
        providerAuthToken: String
    ): ProviderAuthentication = when (type) {
        IP_ADDRESS -> {
            TODO("IP_ADDRESS: Not yet implemented")
        }

        GOOGLE -> {
            TODO("GOOGLE: Not yet implemented")
        }

        APPLE -> {
            TODO("APPLE: Not yet implemented")
        }

        EMAIL_AND_PASSWORD -> throw UnsupportedOperationException("Cannot verify $type provider authentication.")
    }

    @Transactional
    override fun findByIdentity(type: ProviderAuthentication.Type, providerId: String): ProviderAuthentication? {
        TODO("Not yet implemented")
    }
}
