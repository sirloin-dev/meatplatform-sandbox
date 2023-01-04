/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.domain.usecase.auth

import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.auth.RsaCertificate
import org.bouncycastle.jce.provider.BouncyCastleProvider
import test.SharedTestObjects.faker
import test.com.sirloin.util.random.randomEnum
import test.util.randomAlphanumeric
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.util.*

fun ProviderAuthentication.Companion.random(
    type: ProviderAuthentication.Type = randomEnum(ProviderAuthentication.Type::class),
    providerId: String = when (type) {
        ProviderAuthentication.Type.IP_ADDRESS -> "localhost"
        ProviderAuthentication.Type.EMAIL_AND_PASSWORD -> faker.internet().emailAddress()
        ProviderAuthentication.Type.GOOGLE,
        ProviderAuthentication.Type.APPLE -> randomAlphanumeric(24, 24)
    },
    password: String? = if (type == ProviderAuthentication.Type.EMAIL_AND_PASSWORD) {
        randomAlphanumeric(16, 16)
    } else {
        null
    },
    name: String = ""
) = create(
    type = type,
    providerId = providerId,
    password = password,
    name = name
)

fun RsaCertificate.Companion.random(
    id: UUID = UUID.randomUUID(),
    isEnabled: Boolean = true,
    keySize: Int = DEFAULT_KEY_SIZE,
    issuedAt: Instant = Instant.now(),
    activeUntil: Instant = issuedAt.plusSeconds(DEFAULT_CERTIFICATE_ACTIVE_SECONDS)
): RsaCertificate {
    val rawKeyPair = KeyPairGenerator.getInstance(DEFAULT_ALGORITHM, BouncyCastleProvider.PROVIDER_NAME).run {
        initialize(DEFAULT_KEY_SIZE, SecureRandom())
        return@run generateKeyPair()
    }
    val publicKey = rawKeyPair.public as RSAPublicKey
    val privateKey = rawKeyPair.private as RSAPrivateKey

    return create(
        id = id,
        keySize = keySize,
        publicKey = publicKey,
        privateKey = privateKey,
        issuedAt = issuedAt,
        activeUntil = activeUntil,
        isEnabled = isEnabled
    )
}
