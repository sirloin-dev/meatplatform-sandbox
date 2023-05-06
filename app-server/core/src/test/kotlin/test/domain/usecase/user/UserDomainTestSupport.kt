/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.domain.usecase.user

import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.user.User
import net.meatplatform.sandbox.domain.user.usecase.CreateUserUseCase
import test.SharedTestObjects.faker
import test.com.sirloin.util.random.randomEnum
import test.domain.usecase.auth.random
import test.util.randomAlphanumeric
import java.time.Instant
import java.util.*

fun User.Companion.random(
    id: UUID = UUID.randomUUID(),
    nickname: String = faker.funnyName().name(),
    profileImageUrl: String? = if (faker.random().nextBoolean()) {
        faker.internet().image()
    } else {
        null
    },
    authentications: Collection<ProviderAuthentication> = setOf(ProviderAuthentication.random()),
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
