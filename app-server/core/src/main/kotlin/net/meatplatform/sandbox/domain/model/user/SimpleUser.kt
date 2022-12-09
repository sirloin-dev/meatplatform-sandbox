/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.model.user

import net.meatplatform.sandbox.audit.TimestampHolder
import net.meatplatform.sandbox.audit.TimestampHolderImpl
import net.meatplatform.sandbox.audit.indentifiable.UUIDIdentifiable
import java.time.Instant
import java.util.*

/**
 * @since 2022-02-14
 */
interface SimpleUser : UUIDIdentifiable, TimestampHolder {
    val nickname: String

    val profileImageUrl: String?

    companion object {
        const val NICKNAME_SIZE_MIN = 2
        const val NICKNAME_SIZE_MAX = 36

        fun create(
            id: UUID = UUID.randomUUID(),
            nickname: String,
            profileImageUrl: String? = null,
            createdAt: Instant = TimestampHolder.EMPTY_INSTANT,
            updatedAt: Instant = TimestampHolder.EMPTY_INSTANT
        ): SimpleUser {
            return SimpleUserImpl(
                id = id,
                nickname = nickname,
                profileImageUrl = profileImageUrl,
                timestampHolder = TimestampHolderImpl.from(
                    TimestampHolder.create(
                        createdAt = createdAt,
                        updatedAt = updatedAt
                    )
                )
            )
        }
    }
}

internal data class SimpleUserImpl(
    override val id: UUID,
    override val nickname: String,
    override val profileImageUrl: String?,
    val timestampHolder: TimestampHolderImpl
) : SimpleUser, TimestampHolder by timestampHolder {
    companion object {
        fun from(src: SimpleUser) = with(src) {
            if (this is SimpleUserImpl) {
                this
            } else {
                SimpleUserImpl(
                    id = id,
                    nickname = nickname,
                    profileImageUrl = profileImageUrl,
                    timestampHolder = TimestampHolderImpl(
                        createdAt = createdAt,
                        updatedAt = updatedAt
                    )
                )
            }
        }
    }
}
