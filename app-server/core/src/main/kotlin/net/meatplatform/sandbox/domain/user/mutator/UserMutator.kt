/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.user.mutator

import net.meatplatform.sandbox.domain.TimestampHolder
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.user.User
import java.time.Instant
import java.util.*

/**
 * @since 2023-05-06
 */
class UserMutator(
    id: UUID,
    nickname: String,
    profileImageUrl: String?,
    timestampHolder: TimestampHolder,
    override val authentications: List<ProviderAuthentication>
) : SimpleUserMutator(
    id = id,
    nickname = nickname,
    profileImageUrl = profileImageUrl,
    timestampHolder = timestampHolder
), User {
    fun copyUser(
        id: UUID = this.id,
        nickname: String = this.nickname,
        profileImageUrl: String? = this.profileImageUrl,
        authentications: List<ProviderAuthentication> = this.authentications,
        createdAt: Instant = this.createdAt,
        updatedAt: Instant = this.updatedAt
    ): UserMutator {
        val simpleUser = copySimpleUser(
            id = id,
            nickname = nickname,
            profileImageUrl = profileImageUrl,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        return simpleUser.let {
            create(
                id = it.id,
                nickname = it.nickname,
                profileImageUrl = it.profileImageUrl,
                authentications = authentications,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt
            )
        }
    }

    companion object {
        fun from(src: User): UserMutator = with(src) {
            if (this is UserMutator) {
                this
            } else {
                create(
                    id = id,
                    nickname = nickname,
                    profileImageUrl = profileImageUrl,
                    authentications = authentications,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )
            }
        }

        internal fun create(
            id: UUID,
            nickname: String,
            profileImageUrl: String?,
            authentications: Collection<ProviderAuthentication>,
            createdAt: Instant,
            updatedAt: Instant
        ) = UserMutator(
            id = id,
            nickname = nickname,
            profileImageUrl = profileImageUrl,
            authentications = authentications.toList(),
            timestampHolder = TimestampHolder.create(
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        )
    }
}
