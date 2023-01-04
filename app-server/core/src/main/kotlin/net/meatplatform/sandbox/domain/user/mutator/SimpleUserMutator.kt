/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.user.mutator

import net.meatplatform.sandbox.domain.TimestampHolder
import net.meatplatform.sandbox.domain.TimestampHolderMutator
import net.meatplatform.sandbox.domain.user.SimpleUser
import net.meatplatform.sandbox.util.ToStringHelper
import java.time.Instant
import java.util.*

/**
 * @since 2023-05-06
 */
open class SimpleUserMutator(
    override val id: UUID,
    override val nickname: String,
    override val profileImageUrl: String?,
    val timestampHolder: TimestampHolder
) : SimpleUser, TimestampHolder by timestampHolder {
    fun copySimpleUser(
        id: UUID = this.id,
        nickname: String = this.nickname,
        profileImageUrl: String? = this.profileImageUrl,
        createdAt: Instant = this.createdAt,
        updatedAt: Instant = this.updatedAt
    ) = create(
        id = id,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    override fun equals(other: Any?): Boolean =
        other is SimpleUserMutator &&
                id == other.id &&
                nickname == other.nickname &&
                profileImageUrl == other.profileImageUrl &&
                timestampHolder == other.timestampHolder

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + nickname.hashCode()
        result = 31 * result + (profileImageUrl?.hashCode() ?: 0)
        result = 31 * result + timestampHolder.hashCode()
        return result
    }

    override fun toString(): String = ToStringHelper.toString(this)

    companion object {
        fun from(src: SimpleUser) = with(src) {
            if (this is SimpleUserMutator) {
                this
            } else {
                create(
                    id = id,
                    nickname = nickname,
                    profileImageUrl = profileImageUrl,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )
            }
        }

        internal fun create(
            id: UUID,
            nickname: String,
            profileImageUrl: String?,
            createdAt: Instant,
            updatedAt: Instant
        ) = SimpleUserMutator(
            id = id,
            nickname = nickname,
            profileImageUrl = profileImageUrl,
            timestampHolder = TimestampHolder.create(
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        )
    }
}
