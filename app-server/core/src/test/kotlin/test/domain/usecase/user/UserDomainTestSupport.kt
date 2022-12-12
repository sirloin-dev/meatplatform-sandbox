/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.domain.usecase.user

import com.github.javafaker.Faker
import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.model.user.User
import net.meatplatform.sandbox.domain.usecase.user.CreateUserUseCase
import test.com.sirloin.util.random.randomEnum
import test.domain.usecase.auth.random
import test.util.randomAlphanumeric
import java.time.Instant
import java.util.*

/**
 * @since 2022-02-14
 */
data class CreateUserUseCaseMessageImpl(
    override val authenticationType: ProviderAuthentication.Type,
    override val email: String?,
    override val password: String?,
    override val providerAuthToken: String?,
    override val nickname: String,
    override val profileImageUrl: String?
) : CreateUserUseCase.Message {
    companion object {
        fun random(
            authenticationType: ProviderAuthentication.Type = randomEnum(ProviderAuthentication.Type::class),
            email: String? = when (authenticationType) {
                ProviderAuthentication.Type.EMAIL_AND_PASSWORD -> Faker().internet().emailAddress()
                else -> null
            },
            password: String? = when (authenticationType) {
                ProviderAuthentication.Type.EMAIL_AND_PASSWORD -> randomAlphanumeric(16, 16)
                else -> null
            },
            providerAuthToken: String? = when (authenticationType) {
                ProviderAuthentication.Type.EMAIL_AND_PASSWORD -> null
                else -> randomAlphanumeric(min = 128, max = 128)
            },
            nickname: String = Faker().funnyName().name(),
            profileImageUrl: String? = if (Faker().random().nextBoolean()) {
                Faker().internet().image()
            } else {
                null
            }
        ): CreateUserUseCase.Message = CreateUserUseCaseMessageImpl(
            authenticationType = authenticationType,
            email = email,
            password = password,
            providerAuthToken = providerAuthToken,
            nickname = nickname,
            profileImageUrl = profileImageUrl
        )
    }
}

fun User.Companion.random(
    id: UUID = UUID.randomUUID(),
    nickname: String = Faker().funnyName().name(),
    profileImageUrl: String? = if (Faker().random().nextBoolean()) {
        Faker().internet().image()
    } else {
        null
    },
    authentications: Iterable<ProviderAuthentication> = setOf(ProviderAuthentication.random()),
    createdAt: Instant? = null,
    updatedAt: Instant? = null
): User {
    val now = Instant.now()

    return create(
        id = id,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        authentications = authentications,
        createdAt = createdAt ?: now,
        updatedAt = updatedAt ?: now
    )
}
