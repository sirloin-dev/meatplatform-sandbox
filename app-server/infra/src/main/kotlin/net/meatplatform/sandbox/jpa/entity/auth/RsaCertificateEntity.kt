/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.entity.auth

import jakarta.persistence.*
import net.meatplatform.sandbox.domain.auth.RsaCertificate
import net.meatplatform.sandbox.jpa.ValueImporterJpaEntityTemplate
import net.meatplatform.sandbox.jpa.converter.UuidConverter
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemWriter
import java.io.StringWriter
import java.security.Key
import java.time.Instant
import java.util.*

/**
 * @since 2022-12-30
 */
@Entity
@Table(name = "rsa_certificates")
internal class RsaCertificateEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq", unique = true, nullable = false, insertable = false, updatable = false)
    val seq: Long = 0L,

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    @Convert(converter = UuidConverter::class)
    override val id: UUID,

    @Column(name = "is_enabled")
    var isEnabled: Boolean = false,

    @Column(name = "key_size")
    var keySize: Int = 0,

    @Column(name = "public_key", columnDefinition = "TEXT")
    var pemPublicKey: String = "",

    @Column(name = "private_key", columnDefinition = "TEXT")
    var pemPrivateKey: String = "",

    @Column(name = "issued_at")
    var issuedAt: Instant = Instant.MIN,

    @Column(name = "active_until")
    var activeUntil: Instant = Instant.MIN
) : ValueImporterJpaEntityTemplate<UUID, RsaCertificateEntity>() {
    init {
        val now = Instant.now()

        @Suppress("LeakingThis")    // Default 여부만 체크하므로 Leak 하더라도 큰 문제 없음
        issuedAt = issuedAt.takeIf { it != Instant.MIN } ?: now
        @Suppress("LeakingThis")    // Default 여부만 체크하므로 Leak 하더라도 큰 문제 없음
        activeUntil = activeUntil.takeIf { it != Instant.MIN } ?: now
    }

    override fun importValuesInternal(newValue: RsaCertificateEntity) {
        this.isEnabled = newValue.isEnabled
        this.keySize = newValue.keySize
        this.pemPublicKey = newValue.pemPublicKey
        this.pemPrivateKey = newValue.pemPrivateKey
        this.issuedAt = newValue.issuedAt
        this.activeUntil = newValue.activeUntil
    }

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

    companion object {
        fun create(newValue: RsaCertificate): RsaCertificateEntity = RsaCertificateEntity(
            id = if (newValue.isIdentifiable) {
                newValue.id
            } else {
                UUID.randomUUID()
            },
            isEnabled = newValue.isEnabled,
            keySize = newValue.keySize,
            pemPublicKey = newValue.publicKey.toPemFormat(),
            pemPrivateKey = newValue.privateKey.toPemFormat(),
            issuedAt = newValue.issuedAt,
            activeUntil = newValue.activeUntil
        )

        private fun Key.toPemFormat(): String = StringWriter().apply {
            PemWriter(this).use { it.writeObject(PemObject(this@toPemFormat.format, this@toPemFormat.encoded)) }
        }.toString()
    }
}
