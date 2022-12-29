/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.entity.auth

import com.sirloin.jvmlib.util.EMPTY_UUID
import jakarta.persistence.*
import net.meatplatform.sandbox.jpa.converter.UuidConverter
import net.meatplatform.sandbox.jpa.entity.user.UserEntity
import java.time.Instant
import java.util.*

/**
 * Equals, Hashcode 구현에 대한 Article 은 [EvansClassification](https://martinfowler.com/bliki/EvansClassification.html) 참고.
 *
 * @since 2022-12-30
 */
@Entity
@Table(name = "rsa_certificates")
internal class RsaCertificateEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq", unique = true, nullable = false, insertable = false, updatable = false)
    var seq: Long = -1L

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    @Convert(converter = UuidConverter::class)
    var id: UUID = EMPTY_UUID

    @Column(name = "is_enabled")
    var isEnabled: Boolean = false

    @Column(name = "key_size")
    var keySize: Int = 0

    @Column(name = "public_key", columnDefinition = "TEXT")
    var pemPublicKey: String = ""

    @Column(name = "private_key", columnDefinition = "TEXT")
    var pemPrivateKey: String = ""

    @Column(name = "issued_at")
    var issuedAt: Instant = Instant.MIN

    @Column(name = "active_until")
    var activeUntil: Instant = Instant.MIN

    override fun equals(other: Any?): Boolean = if (other is UserEntity) {
        id == other.id
    } else {
        false
    }

    override fun hashCode(): Int = Objects.hash(id)

    override fun toString(): String = """${RsaCertificateEntity::class.simpleName}(
        |  seq=$seq,
        |  id=$id,
        |  isEnabled=$isEnabled,
        |  keySize=$keySize,
        |  pemPublicKey='$pemPublicKey',
        |  pemPrivateKey='$pemPrivateKey',
        |  issuedAt=$issuedAt,
        |  activeUntil=$activeUntil
        |)""".trimMargin()
}
