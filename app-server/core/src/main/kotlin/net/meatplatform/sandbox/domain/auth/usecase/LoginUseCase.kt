/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.auth.usecase

import net.meatplatform.sandbox.annotation.UseCase
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.auth.repository.ProviderAuthRepository
import net.meatplatform.sandbox.domain.auth.usecase.LoginUseCase.*
import net.meatplatform.sandbox.domain.user.User
import net.meatplatform.sandbox.domain.user.repository.UserRepository
import net.meatplatform.sandbox.exception.external.auth.ProviderAuthenticationFailedException
import net.meatplatform.sandbox.exception.external.user.UserByProviderAuthenticationNotFoundException
import net.meatplatform.sandbox.util.PasswordEncoderMixin

/**
 * @since 2023-01-02
 */
interface LoginUseCase {
    fun getUserByProviderAuthentication(message: Message): User = message.run {
        findUserByProviderAuthentication(this) ?: throw UserByProviderAuthenticationNotFoundException(type, token)
    }

    fun findUserByProviderAuthentication(message: Message): User?

    interface Message {
        val type: ProviderAuthentication.Type
        val token: String
    }

    data class EmailLoginMessage(
        val email: String,
        val password: String
    ) : Message {
        override val token: String = email

        override val type: ProviderAuthentication.Type
            get() = ProviderAuthentication.Type.EMAIL_AND_PASSWORD
    }

    data class ThirdPartyAuthLoginMessage(
        override val type: ProviderAuthentication.Type,
        val authToken: String
    ) : Message {
        override val token: String = authToken

        init {
            when (type) {
                ProviderAuthentication.Type.GOOGLE,
                ProviderAuthentication.Type.APPLE -> { /* no-op */ }

                else -> throw IllegalArgumentException("'$type' is not a Third-party OAuth login type.")
            }
        }
    }

    companion object {
        fun newInstance(
            providerAuthRepository: ProviderAuthRepository,
            userRepository: UserRepository
        ): LoginUseCase = LoginUseCaseImpl(
            providerAuths = providerAuthRepository,
            users = userRepository
        )
    }
}

@UseCase
internal class LoginUseCaseImpl(
    private val providerAuths: ProviderAuthRepository,
    private val users: UserRepository
) : LoginUseCase, PasswordEncoderMixin {
    override fun findUserByProviderAuthentication(message: Message): User? {
        val providerAuth = when (message) {
            is EmailLoginMessage -> message.run {
                providerAuths.findByEmailAuthIdentity(email, encodeToPassword(password))
            } ?: return null

            is ThirdPartyAuthLoginMessage -> try {
                message.run {
                    providerAuths.verifyProviderAuth(type, authToken)
                }
            } catch (e: ProviderAuthenticationFailedException) {
                throw UserByProviderAuthenticationNotFoundException(
                    providerType = message.type,
                    providerToken = message.token,
                    cause = e
                )
            }

            else -> throw UnsupportedOperationException("'${message.type}' Login is not supported.")
        }

        return users.findByProviderAuth(providerAuth)
    }
}
