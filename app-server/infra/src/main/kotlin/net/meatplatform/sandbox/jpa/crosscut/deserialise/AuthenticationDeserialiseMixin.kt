/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.crosscut.deserialise

import net.meatplatform.sandbox.annotation.RequiresTransaction
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.auth.RsaCertificate
import net.meatplatform.sandbox.jpa.entity.auth.RsaCertificateEntity
import net.meatplatform.sandbox.jpa.entity.auth.UserAuthenticationEntity
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemReader
import java.io.StringReader
import java.security.Key
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

/**
 * @since 2023-05-07
 */
@RequiresTransaction
internal interface AuthenticationDeserialiseMixin {
    fun UserAuthenticationEntity.toProviderAuthentication(): ProviderAuthentication =
        ProviderAuthentication.create(
            type = type,
            providerId = providerId,
            password = password,
            name = name
        )

    fun RsaCertificateEntity.toRsaCertificate(): RsaCertificate =
        RsaCertificate.create(
            id = id,
            keySize = keySize,
            publicKey = pemPublicKey.toKey() as RSAPublicKey,
            privateKey = pemPrivateKey.toKey() as RSAPrivateKey,
            issuedAt = issuedAt,
            activeUntil = activeUntil,
            isEnabled = isEnabled
        )

    companion object {
        private const val FORMAT_PKCS8 = "PKCS#8"
        private const val FORMAT_X509 = "X.509"

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
    }
}
