/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.repository.auth

import net.meatplatform.sandbox.domain.auth.RsaCertificate
import net.meatplatform.sandbox.jpa.entity.auth.RsaCertificateEntity
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemReader
import org.bouncycastle.util.io.pem.PemWriter
import java.io.StringReader
import java.io.StringWriter
import java.security.Key
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

internal fun RsaCertificateEntity.importValues(newValue: RsaCertificate): RsaCertificateEntity {
    this.id = if (newValue.isIdentifiable) {
        newValue.id
    } else {
        UUID.randomUUID()
    }
    this.isEnabled = newValue.isEnabled
    this.keySize = newValue.keySize
    this.pemPublicKey = newValue.publicKey.toPemFormat()
    this.pemPrivateKey = newValue.privateKey.toPemFormat()
    this.issuedAt = newValue.issuedAt
    this.activeUntil = newValue.activeUntil

    return this
}

internal fun RsaCertificate.toEntity(): RsaCertificateEntity = RsaCertificateEntity().importValues(this)

internal fun RsaCertificate.Companion.fromEntity(
    rsaCertEntity: RsaCertificateEntity
): RsaCertificate = with(rsaCertEntity) {
    create(
        id = id,
        keySize = keySize,
        publicKey = pemPublicKey.toKey() as RSAPublicKey,
        privateKey = pemPrivateKey.toKey() as RSAPrivateKey,
        issuedAt = issuedAt,
        activeUntil = activeUntil,
        isEnabled = isEnabled
    )
}

private const val FORMAT_PKCS8 = "PKCS#8"
private const val FORMAT_X509 = "X.509"

private fun Key.toPemFormat(): String = StringWriter().apply {
    PemWriter(this).use { it.writeObject(PemObject(this@toPemFormat.format, this@toPemFormat.encoded)) }
}.toString()

private fun String.toKey(): Key? = with(StringReader(this)) {
    PemReader(this).use { it.readPemObject() }
}.run {
    return@run when (type) {
        FORMAT_PKCS8 -> keyFactory().generatePrivate(PKCS8EncodedKeySpec(content))
        FORMAT_X509 -> keyFactory().generatePublic(X509EncodedKeySpec(content))
        else -> throw UnsupportedOperationException("$type type of key generation is not supported.")
    }
}

private fun keyFactory() =
    KeyFactory.getInstance(RsaCertificate.DEFAULT_ALGORITHM, BouncyCastleProvider.PROVIDER_NAME)
