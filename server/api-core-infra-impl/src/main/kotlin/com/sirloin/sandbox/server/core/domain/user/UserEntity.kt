package com.sirloin.sandbox.server.core.domain.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

// 도메인 객체 생성에 여러 필드가 필요하기 때문에 불가피
@Suppress("LongParameterList")
@Table("users")
// POINT: 이 클래스를 data class 로 선언하고, equals / hashCode 를 삭제해도 문제가 없을까요?
// POINT: Entity 와 Domain Model 의 차이란 뭘까요?
internal class UserEntity constructor(
    @get:Id
    var id: Long? = null,
    override val uuid: UUID,
    nickname: String,
    profileImageUrl: String,
    override var deletedAt: Instant?,
    override var createdAt: Instant,
    updatedAt: Instant,
    override var version: Long,
) : User.Editor {
    override var nickname: String = ""
        set(value) {
            field = value
            this.updatedAt = Instant.now()
        }

    override var profileImageUrl: String = ""
        set(value) {
            field = value
            this.updatedAt = Instant.now()
        }

    override var updatedAt: Instant = Instant.now()

    init {
        this.nickname = nickname
        this.profileImageUrl = profileImageUrl
        this.updatedAt = updatedAt
    }

    override fun equals(other: Any?): Boolean =
        if (other !is UserEntity) {
            false
        } else {
            Objects.equals(id, other.id) &&
                    Objects.equals(uuid, other.uuid) &&
                    Objects.equals(nickname, other.nickname) &&
                    Objects.equals(profileImageUrl, other.profileImageUrl) &&
                    Objects.equals(deletedAt, other.deletedAt) &&
                    Objects.equals(createdAt, other.createdAt) &&
                    Objects.equals(updatedAt, other.updatedAt) &&
                    Objects.equals(version, other.version)
        }

    override fun hashCode(): Int {
        return Objects.hash(
            this.id,
            this.uuid,
            this.nickname,
            this.profileImageUrl,
            this.deletedAt,
            this.createdAt,
            this.updatedAt,
            this.version
        )
    }

    override fun toString(): String = """UserEntity(
            |  id=$id,
            |  uuid=$uuid,
            |  nickname='$nickname',
            |  profileImageUrl='$profileImageUrl',
            |  deletedAt=${deletedAt},
            |  createdAt=${createdAt},
            |  updatedAt=${updatedAt},
            |  version=$version
            |)""".trimMargin()

    companion object {
        fun from(src: User): UserEntity = with(src) {
            UserEntity(
                id = if (this is UserEntity) id else null,
                uuid = uuid,
                nickname = nickname,
                profileImageUrl = profileImageUrl,
                deletedAt = deletedAt,
                createdAt = createdAt,
                updatedAt = updatedAt,
                version = version
            )
        }
    }
}
