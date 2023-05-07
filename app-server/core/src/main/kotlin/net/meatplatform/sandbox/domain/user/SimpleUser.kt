/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.user

import com.sirloin.jvmlib.util.EMPTY_UUID
import net.meatplatform.sandbox.domain.IdHolderUUID
import net.meatplatform.sandbox.domain.TimestampHolder
import net.meatplatform.sandbox.domain.user.mutator.SimpleUserMutator
import java.time.Instant
import java.util.*

/**
 * @since 2022-02-14
 */
interface SimpleUser : IdHolderUUID, TimestampHolder {
    val nickname: String

    val profileImageUrl: String?

    fun mutator(): SimpleUserMutator = SimpleUserMutator.from(this)

    companion object {
        const val NICKNAME_SIZE_MIN = 2
        const val NICKNAME_SIZE_MAX = 36

        fun create(
            id: UUID = EMPTY_UUID,
            nickname: String,
            profileImageUrl: String? = null,
            createdAt: Instant = TimestampHolder.EMPTY_INSTANT,
            updatedAt: Instant = TimestampHolder.EMPTY_INSTANT
        ): SimpleUser = SimpleUserMutator.create(
            id = id,
            nickname = nickname,
            profileImageUrl = profileImageUrl,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
