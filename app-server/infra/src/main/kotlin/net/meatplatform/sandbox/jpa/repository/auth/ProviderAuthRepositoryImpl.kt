/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.repository.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.sirloin.jvmlib.annotation.VisibleForTesting
import net.meatplatform.sandbox.CoreApplication
import net.meatplatform.sandbox.annotation.InfrastructureService
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication.Type.*
import net.meatplatform.sandbox.domain.auth.repository.ProviderAuthRepository
import net.meatplatform.sandbox.http.SandboxHttpClient
import net.meatplatform.sandbox.jpa.crosscut.deserialise.AuthenticationDeserialiseMixin
import net.meatplatform.sandbox.jpa.dao.read.UserAuthenticationEntityReadDao
import net.meatplatform.sandbox.jpa.repository.auth.internal.AppleOAuthVerifier
import net.meatplatform.sandbox.jpa.repository.auth.internal.GoogleOAuthVerifier
import net.meatplatform.sandbox.util.PasswordCodecMixin
import org.springframework.transaction.annotation.Transactional

/**
 * @since 2022-02-14
 */
@InfrastructureService(ProviderAuthRepository.NAME)
internal class ProviderAuthRepositoryImpl(
    application: CoreApplication,
    objectMapper: ObjectMapper,
    httpClient: SandboxHttpClient,
    private val providerAuthDao: UserAuthenticationEntityReadDao
) : ProviderAuthRepository, AuthenticationDeserialiseMixin, PasswordCodecMixin {
    @VisibleForTesting
    internal var googleOAuthVerifier = GoogleOAuthVerifier(
        application.oAuthConfig().googleOAuthUrl,
        httpClient,
        objectMapper
    )

    @VisibleForTesting
    internal var appleOAuthVerifier = AppleOAuthVerifier(
        application.oAuthConfig().appleOAuthUrl,
        httpClient,
        objectMapper
    )

    override fun verifyProviderAuth(
        type: ProviderAuthentication.Type,
        providerAuthToken: String
    ): ProviderAuthentication = when (type) {
        GOOGLE -> googleOAuthVerifier.verify(providerAuthToken)
        APPLE -> appleOAuthVerifier.verify(providerAuthToken)
        IP_ADDRESS, EMAIL_AND_PASSWORD ->
            throw UnsupportedOperationException("Cannot verify $type provider authentication.")
    }

    @Transactional
    override fun findByEmailAuthIdentity(email: String, encodedPassword: String): ProviderAuthentication? =
        providerAuthDao.findByIdentity(
            ProviderAuthentication.Type.EMAIL_AND_PASSWORD,
            email,
            encodedPassword
        )?.toProviderAuthentication()

    @Transactional
    override fun findByProviderAuthIdentity(
        type: ProviderAuthentication.Type,
        providerId: String
    ): ProviderAuthentication? =
        providerAuthDao.findByIdentity(type, providerId)?.toProviderAuthentication()
}
