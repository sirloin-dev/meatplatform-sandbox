/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.domain.user

import com.sirloin.jvmlib.time.truncateToSeconds
import com.sirloin.sandbox.server.core.model.DateAuditable
import com.sirloin.sandbox.server.core.model.Editable
import com.sirloin.sandbox.server.core.model.Versioned
import java.time.Instant
import java.util.*

/**
 * 애플리케이션이 정의한 User 도메인 모델.
 *
 * @since 2022-02-14
 */
interface User : DateAuditable, Versioned<Long>, Editable<User> {
    val uuid: UUID

    val nickname: String

    val profileImageUrl: String

    val deletedAt: Instant?

    val isDeleted: Boolean
        get() = deletedAt != null

    override fun edit(): Editor

    // POINT: 왜 User 도메인 모델을 mutable 로 설계하지 않고, 이런 타입을 별도로 만들었을까요?
    interface Editor : User {
        override var nickname: String

        override var profileImageUrl: String

        /**
         * 이 필드에 직접 assign 하지 마시고, delete 메소드를 활용하세요.
         * 이 필드에 직접 assign 할 경우 문제가 생길 수 있습니다.
         */
        override var deletedAt: Instant?

        override var updatedAt: Instant

        override fun edit(): Editor = this

        fun delete(instant: Instant = Instant.now().truncateToSeconds()): Editor {
            this.updatedAt = instant
            this.deletedAt = instant
            return this
        }
    }

    companion object {
        const val NICKNAME_SIZE_MIN = 2
        const val NICKNAME_SIZE_MAX = 32

        internal data class Model(
            override val uuid: UUID,
            override var nickname: String,
            override var profileImageUrl: String,
            override var deletedAt: Instant?,
            override val createdAt: Instant,
            override var updatedAt: Instant,
            override val version: Long
        ) : Editor

        // 도메인 객체 생성에 여러 필드가 필요하기 때문에 불가피
        @Suppress("LongParameterList")
        fun create(
            uuid: UUID? = null,
            nickname: String,
            profileImageUrl: String,
            deletedAt: Instant? = null,
            createdAt: Instant? = null,
            updatedAt: Instant? = null,
            version: Long? = null
        ): User {
            val now = Instant.now().truncateToSeconds()

            return Model(
                uuid = uuid ?: UUID.randomUUID(),
                nickname = nickname,
                profileImageUrl = profileImageUrl,
                deletedAt = deletedAt?.truncateToSeconds(),
                createdAt = createdAt?.truncateToSeconds() ?: now,
                updatedAt = updatedAt?.truncateToSeconds() ?: now,
                version = version ?: Versioned.DEFAULT_LONG_INT
            )
        }

        fun from(user: User): User = with(user) {
            create(
                uuid = uuid,
                nickname = nickname,
                profileImageUrl = profileImageUrl,
                deletedAt = deletedAt?.truncateToSeconds(),
                createdAt = createdAt.truncateToSeconds(),
                updatedAt = updatedAt.truncateToSeconds(),
                version = version
            )
        }
    }
}
