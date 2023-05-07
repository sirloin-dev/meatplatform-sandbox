/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.repository.auth

import com.sirloin.jvmlib.annotation.VisibleForTesting
import com.sirloin.jvmlib.util.FastCollectedLruCache
import net.meatplatform.sandbox.annotation.InfrastructureService
import net.meatplatform.sandbox.domain.auth.RsaCertificate
import net.meatplatform.sandbox.domain.auth.RsaCertificate.Companion.DEFAULT_ALGORITHM
import net.meatplatform.sandbox.domain.auth.repository.RsaCertificateRepository
import net.meatplatform.sandbox.jpa.crosscut.deserialise.AuthenticationDeserialiseMixin
import net.meatplatform.sandbox.jpa.dao.read.RsaCertificateEntityReadDao
import net.meatplatform.sandbox.jpa.dao.write.RsaCertificateEntityWriteDao
import net.meatplatform.sandbox.jpa.entity.user.UserEntity.Companion.toRsaCertificateEntity
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.transaction.annotation.Transactional
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * 인증토큰 검증을 위해 Repository 를 매우 자주 호출할 것으로 예상되므로,
 * Memory Cache 를 활용해 DB call 을 가급적 줄이도록 구현했습니다.
 *
 * @since 2022-12-26
 */
@InfrastructureService(RsaCertificateRepository.NAME)
internal class RsaCertificateRepositoryImpl(
    private val policy: RsaCertificate.Policy,
    private val certReader: RsaCertificateEntityReadDao,
    private val certWriter: RsaCertificateEntityWriteDao
) : RsaCertificateRepository, AuthenticationDeserialiseMixin {
    @VisibleForTesting
    internal var lruCache = FastCollectedLruCache.create<UUID, RsaCertificate>(policy.cacheCapacity)

    @VisibleForTesting
    internal var cachedLatestActive: RsaCertificate? = null

    @Transactional
    override fun findById(id: UUID): RsaCertificate? =
        synchronized(CACHE_ACCESS_LOCK) { lruCache.get(id) }
            ?: certReader.findById(id)?.toRsaCertificate()?.also { cacheCertificate(it) }

    @Transactional
    override fun findCurrentlyActive(): RsaCertificate? {
        val now = Instant.now()
        val maybeCachedLastActive = synchronized(LAST_ACTIVE_ACCESS_LOCK) { cachedLatestActive }

        return if (maybeCachedLastActive?.isActiveAt(now) == true) {
            maybeCachedLastActive
        } else {
            certReader.findLatestActiveUntil(now)?.toRsaCertificate()
                ?.also {
                    cacheCertificate(it)
                    synchronized(LAST_ACTIVE_ACCESS_LOCK) { cachedLatestActive = it }
                }
        }
    }

    override fun issueRandom(issuedAt: Instant): RsaCertificate {
        val rawKeyPair = KeyPairGenerator.getInstance(DEFAULT_ALGORITHM, BouncyCastleProvider.PROVIDER_NAME).run {
            initialize(policy.keySize, SecureRandom())
            return@run generateKeyPair()
        }

        return RsaCertificate.create(
            keySize = policy.keySize,
            publicKey = rawKeyPair.public as RSAPublicKey,
            privateKey = rawKeyPair.private as RSAPrivateKey,
            issuedAt = issuedAt,
            activeUntil = issuedAt.plus(policy.activeSeconds, ChronoUnit.SECONDS)
        )
    }

    @Transactional
    override fun save(certificate: RsaCertificate): RsaCertificate {
        val newValues = certificate.toRsaCertificateEntity()
        val certEntity = certificate.run {
            if (isIdentifiable) {
                certReader.findById(id)?.importValues(newValues) ?: newValues
            } else {
                newValues
            }
        }

        return certWriter.save(certEntity).toRsaCertificate().also { cacheCertificate(it) }
    }

    private fun cacheCertificate(cert: RsaCertificate): RsaCertificate = synchronized(CACHE_ACCESS_LOCK) {
        return@synchronized cert.also {
            if (it.isEnabled) {
                lruCache.put(it.id, it)
            } else {
                lruCache.remove(it.id)
            }
        }
    }

    companion object {
        private val CACHE_ACCESS_LOCK = Any()
        private val LAST_ACTIVE_ACCESS_LOCK = Any()
    }
}
