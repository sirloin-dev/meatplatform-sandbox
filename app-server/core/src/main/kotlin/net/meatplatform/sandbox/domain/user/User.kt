/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.user

import com.sirloin.jvmlib.util.EMPTY_UUID
import net.meatplatform.sandbox.domain.TimestampHolder
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.user.mutator.UserMutator
import java.time.Instant
import java.util.*

/**
 * @since 2022-02-14
 */
interface User : SimpleUser {
    val authentications: List<ProviderAuthentication>

    override fun mutator(): UserMutator = UserMutator.from(this)

    companion object {
        fun create(
            id: UUID = EMPTY_UUID,
            nickname: String,
            profileImageUrl: String? = null,
            authentications: Collection<ProviderAuthentication>,
            createdAt: Instant = TimestampHolder.EMPTY_INSTANT,
            updatedAt: Instant = TimestampHolder.EMPTY_INSTANT
        ): User = UserMutator.create(
            id = id,
            nickname = nickname,
            profileImageUrl = profileImageUrl,
            authentications = authentications,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
