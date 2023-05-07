/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.entity.auth

import jakarta.persistence.*
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.jpa.ValueImporterJpaEntityTemplate
import net.meatplatform.sandbox.jpa.converter.ProviderAuthenticationTypeConverter
import net.meatplatform.sandbox.jpa.entity.user.UserEntity

/**
 * @since 2022-12-09
 */
@Entity
@Table(name = "users_authentications")
internal class UserAuthenticationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    val seq: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var ownerUserEntity: UserEntity? = null,

    @Convert(converter = ProviderAuthenticationTypeConverter::class)
    @Column(name = "type")
    var type: ProviderAuthentication.Type,

    @Column(name = "provider_id")
    var providerId: String,

    @Column(name = "password", columnDefinition = "TEXT")
    var password: String?,

    @Column(name = "name")
    var name: String
) : ValueImporterJpaEntityTemplate<Long, UserAuthenticationEntity>() {
    @Version
    @Column(name = "version")
    private var version: Long = 1L

    @get:Transient
    override val id: Long
        get() = seq

    override fun importValuesInternal(newValue: UserAuthenticationEntity) {
        this.ownerUserEntity = newValue.ownerUserEntity
        this.type = newValue.type
        this.providerId = newValue.providerId
        this.password = newValue.password
        this.name = newValue.name
    }

    override fun toString(): String = """${UserAuthenticationEntity::class.simpleName}(
        |  seq=$seq,
        |  user=LAZY ${UserEntity::class.simpleName},
        |  type=$type,
        |  providerId=$providerId,
        |  password=$password,
        |  name=$name,
        |  version=$version
        |)""".trimMargin()

    companion object {
        fun create(newValue: ProviderAuthentication, owner: UserEntity?): UserAuthenticationEntity =
            UserAuthenticationEntity(
                ownerUserEntity = owner,
                type = newValue.type,
                providerId = newValue.providerId,
                password = newValue.password,
                name = newValue.name
            )
    }
}
