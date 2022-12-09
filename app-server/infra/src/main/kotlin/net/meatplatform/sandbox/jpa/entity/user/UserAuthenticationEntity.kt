/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.entity.user

import jakarta.persistence.*
import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication
import net.meatplatform.sandbox.jpa.converter.ProviderAuthenticationTypeConverter
import java.util.*

/**
 * Equals, Hashcode 구현에 대한 Article 은 [EvansClassification](https://martinfowler.com/bliki/EvansClassification.html) 참고.
 *
 * @since 2022-12-09
 */
@Entity
@Table(name = "users_authentications")
internal class UserAuthenticationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    var seq: Long = -1L

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity? = null

    @Convert(converter = ProviderAuthenticationTypeConverter::class)
    @Column(name = "type")
    var type: ProviderAuthentication.Type = ProviderAuthentication.Type.EMAIL_AND_PASSWORD

    @Column(name = "provider_id")
    var providerId: String = ""

    @Lob
    @Column(name = "password", columnDefinition = "TEXT")
    var password: String? = null

    @Column(name = "name")
    var name: String = ""

    @Version
    @Column(name = "version")
    var version: Long = 1L

    override fun equals(other: Any?): Boolean = if (other is UserAuthenticationEntity) {
        seq == other.seq
    } else {
        false
    }

    override fun hashCode(): Int = Objects.hash(seq)

    override fun toString(): String = """${UserAuthenticationEntity::class.simpleName}(
        |  seq=$seq,
        |  user=LAZY ${UserEntity::class.simpleName},
        |  type=$type,
        |  providerId=$providerId,
        |  password=$password,
        |  name=$name,
        |  version=$version
        |)""".trimMargin()
}
