/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.entity.user

import jakarta.persistence.*
import net.meatplatform.sandbox.annotation.RequiresTransaction
import net.meatplatform.sandbox.domain.user.User
import net.meatplatform.sandbox.jpa.ValueImporterJpaEntityTemplate
import net.meatplatform.sandbox.jpa.converter.UuidConverter
import net.meatplatform.sandbox.jpa.crosscut.serialise.AuthenticationSerialiseMixin
import net.meatplatform.sandbox.jpa.entity.auth.UserAuthenticationEntity
import net.meatplatform.sandbox.jpa.entity.common.SoftDeletableEmbedded
import java.util.*

/**
*
 * @since 2022-12-09
 */
@Entity
@Table(name = "users")
internal class UserEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq", unique = true, nullable = false, insertable = false, updatable = false)
    val seq: Long = 0L,

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    @Convert(converter = UuidConverter::class)
    override val id: UUID,

    @Column(name = "nickname")
    var nickname: String,

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    var profileImageUrl: String?,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], mappedBy = "ownerUserEntity", orphanRemoval = true)
    val linkedAuthenticationEntities: MutableSet<UserAuthenticationEntity> = mutableSetOf(),

    @Embedded
    val softDelete: SoftDeletableEmbedded = SoftDeletableEmbedded()
) : ValueImporterJpaEntityTemplate<UUID, UserEntity>() {
    @Version
    @Column(name = "version")
    private var version: Long = 1L

    @RequiresTransaction
    override fun importValuesInternal(newValue: UserEntity) {
        this.nickname = newValue.nickname
        this.profileImageUrl = newValue.profileImageUrl
        this.linkedAuthenticationEntities.apply {
            mergeMany(newValue.linkedAuthenticationEntities)
            onEach { it.ownerUserEntity = this@UserEntity }
        }
        this.softDelete.importValues(newValue.softDelete)
    }

    override fun toString(): String = """${UserEntity::class.simpleName}(
        |  seq=$seq,
        |  id=$id,
        |  nickname='$nickname',
        |  profileImageUrl=$profileImageUrl,
        |  softDelete=$softDelete,
        |  version=$version,
        |  linkedAuthenticationEntities=[LAZY ${UserAuthenticationEntity::class.simpleName}]
        |)""".trimMargin()

    companion object : AuthenticationSerialiseMixin {
        fun create(newValue: User) = UserEntity(
            id = if (newValue.isIdentifiable) {
                newValue.id
            } else {
                UUID.randomUUID()
            },
            nickname = newValue.nickname,
            profileImageUrl = newValue.profileImageUrl
        ).apply {
            this.softDelete.createdAt = newValue.createdAt
            this.softDelete.updatedAt = newValue.updatedAt
            this.linkedAuthenticationEntities.addAll(newValue.authentications.map {
                it.toUserAuthenticationEntity(this)
            })
        }
    }
}
