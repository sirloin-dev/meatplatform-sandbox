/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.user.usecase

import net.meatplatform.sandbox.annotation.UseCase
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.auth.repository.ProviderAuthRepository
import net.meatplatform.sandbox.domain.user.User
import net.meatplatform.sandbox.domain.user.repository.UserRepository
import net.meatplatform.sandbox.exception.external.user.UserWithProviderIdentityAlreadyExist
import net.meatplatform.sandbox.util.PasswordEncoderMixin

/**
 * @since 2022-02-14
 */
interface CreateUserUseCase {
    fun createUser(message: Message, ipAddressStr: String): User

    interface Message {
        val authenticationType: ProviderAuthentication.Type
        val email: String?
        val password: String?
        val providerAuthToken: String?
        val nickname: String
        val profileImageUrl: String?
    }

    companion object {
        fun newInstance(
            providerAuthRepository: ProviderAuthRepository,
            userRepository: UserRepository
        ): CreateUserUseCase = CreateUserUseCaseImpl(
            providerAuths = providerAuthRepository,
            users = userRepository
        )
    }
}

@UseCase
internal class CreateUserUseCaseImpl(
    private val providerAuths: ProviderAuthRepository,
    private val users: UserRepository
) : CreateUserUseCase, PasswordEncoderMixin {
    override fun createUser(message: CreateUserUseCase.Message, ipAddressStr: String): User {
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

                        else -> providerAuths.verifyProviderAuth(authenticationType, providerAuthToken!!)
                    }
                )
            )
        }

        // 중복 가입 여부 검사
        with(newUser.authentications.first()) {
            val maybePrevious = users.findByProviderAuth(this)
            if (maybePrevious != null) {
                throw UserWithProviderIdentityAlreadyExist(type, providerId)
            }
        }

        return users.save(newUser).run {
            mutator().copyUser(authentications = authentications.toMutableList().apply {
                add(
                    // TO-DO-20221225: IPv6 케이스는 대응하지 않음
                    ProviderAuthentication.create(
                        type = ProviderAuthentication.Type.IP_ADDRESS,
                        providerId = ipAddressStr
                    )
                )
            })
        }
    }
}
