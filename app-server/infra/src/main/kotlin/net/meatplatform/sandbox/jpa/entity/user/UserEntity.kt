/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.entity.user

import com.sirloin.jvmlib.util.EMPTY_UUID
import jakarta.persistence.*
import net.meatplatform.sandbox.jpa.entity.common.SoftDeletableEmbedded
import java.util.*

/**
 * Equals, Hashcode 구현에 대한 Article 은 [EvansClassification](https://martinfowler.com/bliki/EvansClassification.html) 참고.
 *
 * @since 2022-12-09
 */
@Entity
@Table(name = "users")
internal class UserEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq", unique = true, nullable = false, insertable = false, updatable = false)
    var seq: Long = -1L

    @Id
    @Column(name = "id")
    var id: UUID = EMPTY_UUID

    @Column(name = "nickname")
    var nickname: String = ""

    @Lob
    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    var profileImageUrl: String? = null

    @Embedded
    val softDelete: SoftDeletableEmbedded = SoftDeletableEmbedded()

    @Version
    @Column(name = "version")
    var version: Long = 1L

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], mappedBy = "user")
    var linkedAuthenticationEntities: MutableSet<UserAuthenticationEntity> = mutableSetOf()

    override fun equals(other: Any?): Boolean = if (other is UserEntity) {
        id == other.id
    } else {
        false
    }

    override fun hashCode(): Int = Objects.hash(id)

    override fun toString(): String = """${UserEntity::class.simpleName}(
        |  seq=$seq,
        |  id=$id,
        |  nickname='$nickname',
        |  profileImageUrl=$profileImageUrl,
        |  softDelete=$softDelete,
        |  version=$version,
        |  linkedAuthenticationEntities=[LAZY ${UserAuthenticationEntity::class.simpleName}]
        |)""".trimMargin()
}
