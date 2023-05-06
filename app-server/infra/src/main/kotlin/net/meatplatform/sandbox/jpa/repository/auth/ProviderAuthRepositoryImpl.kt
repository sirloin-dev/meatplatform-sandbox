/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.repository.auth

import net.meatplatform.sandbox.annotation.InfrastructureService
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication.Type.*
import net.meatplatform.sandbox.domain.auth.repository.ProviderAuthRepository
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
        GOOGLE -> {
            TODO("GOOGLE: Not yet implemented")
        }

        APPLE -> {
            TODO("APPLE: Not yet implemented")
        }

        IP_ADDRESS, EMAIL_AND_PASSWORD ->
            throw UnsupportedOperationException("Cannot verify $type provider authentication.")
    }

    override fun findByEmailAuthIdentity(email: String, password: String): ProviderAuthentication? {
        TODO("Not yet implemented")
    }

    override fun findByProviderAuthIdentity(
        type: ProviderAuthentication.Type,
        providerId: String
    ): ProviderAuthentication? {
        TODO("Not yet implemented")
    }
}
