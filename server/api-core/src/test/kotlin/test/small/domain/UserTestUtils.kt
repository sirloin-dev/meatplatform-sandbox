/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package test.small.domain

import com.github.javafaker.Faker
import com.sirloin.sandbox.server.core.domain.user.User
import java.time.Instant
import java.util.*

fun randomUser(
    uuid: UUID? = null,
    nickname: String? = null,
    profileImageUrl: String? = null,
    createdAt: Instant? = null,
    updatedAt: Instant? = null,
    version: Long? = null
): User = with(Faker()) {
    User.create(
        uuid = uuid,
        nickname = nickname ?: name().username(),
        profileImageUrl = profileImageUrl ?: internet().image(),
        createdAt = createdAt,
        updatedAt = updatedAt,
        version = version
    )
}
