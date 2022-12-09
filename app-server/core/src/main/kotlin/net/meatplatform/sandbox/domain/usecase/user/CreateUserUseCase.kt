/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.usecase.user

import net.meatplatform.sandbox.annotation.UseCase
import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.model.user.User
import net.meatplatform.sandbox.domain.repository.auth.ProviderAuthRepository
import net.meatplatform.sandbox.domain.repository.user.UserRepository
import net.meatplatform.sandbox.util.PasswordEncoderMixin

/**
 * @since 2022-02-14
 */
interface CreateUserUseCase {
    fun createUser(message: Message): User

    interface Message {
        val authenticationType: ProviderAuthentication.Type
        val email: String?
        val password: String?
        val providerAuthToken: String?
        val nickname: String
        val profileImageUrl: String?
    }
}

@UseCase
internal class CreateUserUseCaseImpl(
    private val providerAuths: ProviderAuthRepository,
    private val users: UserRepository
) : CreateUserUseCase, PasswordEncoderMixin {
    override fun createUser(message: CreateUserUseCase.Message): User {
        val newUser = message.run {
            val encodedPassword = password?.let { encodeToPassword(it) }

            return@run User.create(
                nickname = nickname,
                profileImageUrl = profileImageUrl,
                authentications = setOf(
                    when (authenticationType) {
                        ProviderAuthentication.Type.EMAIL_AND_PASSWORD -> ProviderAuthentication.create(
                            type = authenticationType,
                            providerId = email!!,
                            password = encodedPassword,
                            name = ""
                        )

                        else -> providerAuths.verify(authenticationType, providerAuthToken!!)
                    }
                )
            )
        }

        return users.save(newUser)
    }
}
