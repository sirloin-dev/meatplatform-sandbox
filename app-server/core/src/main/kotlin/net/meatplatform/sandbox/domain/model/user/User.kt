/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.model.user

import com.sirloin.jvmlib.util.EMPTY_UUID
import net.meatplatform.sandbox.audit.TimestampHolder
import net.meatplatform.sandbox.audit.TimestampHolderImpl
import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.model.auth.ProviderAuthenticationImpl
import java.time.Instant
import java.util.*

/**
 * @since 2022-02-14
 */
interface User : SimpleUser {
    val authentications: List<ProviderAuthentication>

    companion object {
        fun create(
            id: UUID = EMPTY_UUID,
            nickname: String,
            profileImageUrl: String? = null,
            authentications: Iterable<ProviderAuthentication>,
            createdAt: Instant = TimestampHolder.EMPTY_INSTANT,
            updatedAt: Instant = TimestampHolder.EMPTY_INSTANT
        ): User {
            return UserImpl(
                id = id,
                nickname = nickname,
                profileImageUrl = profileImageUrl,
                authentications = authentications.map { ProviderAuthenticationImpl.from(it) },
                timestampHolder = TimestampHolderImpl.from(
                    TimestampHolder.create(
                        createdAt = createdAt,
                        updatedAt = updatedAt
                    )
                ),
            )
        }
    }
}

internal data class UserImpl(
    override val id: UUID,
    override val nickname: String,
    override val profileImageUrl: String?,
    override val authentications: List<ProviderAuthentication>,
    val timestampHolder: TimestampHolderImpl
) : User, TimestampHolder by timestampHolder {
    companion object {
        fun from(src: User): UserImpl = with(src) {
            if (this is UserImpl) {
                this
            } else {
                UserImpl(
                    id = id,
                    nickname = nickname,
                    profileImageUrl = profileImageUrl,
                    authentications = authentications,
                    timestampHolder = TimestampHolderImpl(
                        createdAt = createdAt,
                        updatedAt = updatedAt
                    )
                )
            }
        }
    }
}
