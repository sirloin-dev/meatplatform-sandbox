/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.model.auth

import net.meatplatform.sandbox.audit.indentifiable.UUIDIdentifiable
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.util.*

/**
 * @since 2022-12-26
 */
interface RsaCertificate : UUIDIdentifiable {
    /**
     * RSA 키의 크기.
     *
     * 다만 실제로 2022년 3월 현재 공격으로 키를 뚫는다고 할 때,
     * RSA 1024 의 경우 대략 3년, RSA 2048 의 경우 대략 300조 년 정도 걸린다.
     *
     * 따라서 키 크기는 적당하게 잡아주는 편이 좋다.
     */
    val keySize: Int

    val publicKey: RSAPublicKey

    val privateKey: RSAPrivateKey

    val issuedAt: Instant

    /**
     * 이 시간 이후에는 이 키를 더 이상 적극적으로 사용하지 않고 새로운 키를 발급해야 한다.
     *
     * 하지만 클라이언트는 이미 오래전에 발급한 키를 들고 있을 수도 있다. 따라서 어떤 키의 유효성을 판단하는 용도로
     * 이 필드를 사용하면 안된다. 키 유효성 판단은 [isEnabled] 로만 할 것.
     *
     * 이 값을 짧게 잡으면 Key spin 이 활발하게 일어나 서버의 보안이 상당히 강력해 지지만 그만큼 다양한 Key 를 해독해야
     * 하는 문제가 생긴다. 반면 이 값을 길게 잡으면 서버의 보안이 취약해지는 대신 Key 해독의 수고가 줄어든다.
     * Key 의 크기에 따라 이 값을 적당히 조절하도록 한다. 가령, RSA 1024 를 이용한다면 [issuedAt] 로부터 3년 미만으로
     * 설정하는 편이 좋다.
     */
    val activeUntil: Instant

    fun isActiveAt(timestamp: Instant = Instant.now()): Boolean =
        timestamp < activeUntil

    /**
     * 특정 Key 가 뚫렸을 때(Compromised) 해당 키를 쓰지 못하게 하기 위한 용도.
     * 이 값을 false 로 선언한 순간 해당 키를 이용하는 클라이언트의 문서는 모두 쓸모 없어진다.
     * (가령 로그인 토큰을 구현한 경우, 로그인을 더 이상 할 수 없게 된다 = 로그인이 풀림)
     */
    val isEnabled: Boolean

    interface Policy {
        val cacheCapacity: Int

        /**
         * Key spin 을 위한 기간. 이 지정 주기마다 새로운 Key 를 생성합니다.
         */
        val activeSeconds: Long

        /**
         * RSA Modulus size.
         */
        val keySize: Int

        companion object {
            fun create(
                cacheCapacity: Int = DEFAULT_CERTIFICATES_CACHE_CAPACITY,
                activeSeconds: Long = DEFAULT_CERTIFICATE_ACTIVE_SECONDS,
                keySize: Int = DEFAULT_KEY_SIZE
            ): Policy = RsaCertificatePolicyImpl(
                cacheCapacity = cacheCapacity,
                activeSeconds = activeSeconds,
                keySize = keySize
            )
        }
    }

    companion object {
        /**
         * 인증서는 한번 발급하면 변경할 일이 거의 없기 때문에, 캐시합니다.
         * 이 값은 추후 정책 등으로 구현해 주세요.
         *
         * PEM 포맷의 인증서 1개당 약 메모리를 4KiB 정도 차지하므로, 100개라면 4MiB 의 메모리를 사용합니다.
         */
        const val DEFAULT_CERTIFICATES_CACHE_CAPACITY = 100
        const val DEFAULT_CERTIFICATE_ACTIVE_SECONDS = 24 * 60 * 60L
        const val DEFAULT_KEY_SIZE = 2048

        const val DEFAULT_ALGORITHM = "RSA"

        fun create(
            id: UUID = UUID.randomUUID(),
            isEnabled: Boolean = true,
            keySize: Int,
            publicKey: RSAPublicKey,
            privateKey: RSAPrivateKey,
            issuedAt: Instant,
            activeUntil: Instant
        ): RsaCertificate {
            val now = Instant.now()
            if (activeUntil < now) {
                throw IllegalArgumentException("activeUntil($activeUntil) < now($now)")
            }

            return RsaCertificateImpl(
                id = id,
                isEnabled = isEnabled,
                keySize = keySize,
                publicKey = publicKey,
                privateKey = privateKey,
                issuedAt = issuedAt,
                activeUntil = activeUntil
            )
        }
    }
}

internal data class RsaCertificateImpl(
    override val id: UUID,
    override val isEnabled: Boolean,
    override val keySize: Int,
    override val publicKey: RSAPublicKey,
    override val privateKey: RSAPrivateKey,
    override val issuedAt: Instant,
    override val activeUntil: Instant
) : RsaCertificate

internal data class RsaCertificatePolicyImpl(
    override val cacheCapacity: Int,
    override val activeSeconds: Long,
    override val keySize: Int
) : RsaCertificate.Policy
