/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.domain.repository.auth

import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.auth.repository.ProviderAuthRepository
import test.domain.usecase.auth.random
import test.util.randomAlphanumeric

typealias OnVerifyProviderAuth = (ProviderAuthentication.Type, String) -> ProviderAuthentication

/**
 * @since 2022-02-14
 */
class SpyProviderAuthRepository(
    private val delegate: ProviderAuthRepository
) : ProviderAuthRepository {
    private val mockProviderAuthVerified = HashMap<Pair<ProviderAuthentication.Type, String>, OnVerifyProviderAuth>()
    private val mockEmailAuthIdentity = HashMap<Pair<String, String>, ProviderAuthentication?>()
    private val mockProviderAuthIdentity = HashMap<Pair<ProviderAuthentication.Type, String>, ProviderAuthentication?>()
    private val mockProviderAuthToIdMap = HashMap<Pair<ProviderAuthentication.Type, String>, String>()

    /**
     * 제3자 인증 실패를 시뮬레이션 하기 위해 [onVerified] 가 예외를 던지도록 할 수 있습니다. 이 때는 반환값이 `null` 입니다.
     *
     * 항상 성공하는 [onVerified] 를 주입했거나 기본값을 쓰도록 했다면 반환결과는 절대로 `null` 이 아닙니다.
     */
    fun setProviderAuthVerified(
        type: ProviderAuthentication.Type?,
        providerAuthToken: String?,
        onVerified: OnVerifyProviderAuth = { paType, paToken ->
            val key = mockVerifyProviderAuthKey(paType, paToken)
            val providerId = if (mockProviderAuthToIdMap.containsKey(key)) {
                mockProviderAuthToIdMap[key]!!
            } else {
                randomAlphanumeric(24, 24).also { mockProviderAuthToIdMap[key] = it }
            }

            /* return@lambda */ ProviderAuthentication.random(type = paType, providerId = providerId)
        }
    ): ProviderAuthentication? {
        if (type == null || providerAuthToken == null) {
            throw IllegalArgumentException(
                "ProviderAuthentication, ProviderAuthToken 은 모두 null 이 아니어야 합니다" +
                        "(ProviderAuthentication=$type, ProviderAuthToken=$providerAuthToken)."
            )
        }

        val key = mockVerifyProviderAuthKey(type, providerAuthToken)
        val result = try {
            onVerified.invoke(type, providerAuthToken)
        } catch (_: Throwable) {
            null
        }

        mockProviderAuthVerified[key] = onVerified
        return result
    }

    fun setFindByEmailAuthIdentity(
        email: String,
        password: String,
        onFindByEmailAuthIdentity: ((String, String) -> ProviderAuthentication?)? = null
    ): ProviderAuthentication? {
        val key = mockVerifyEmailAuthKey(email, password)
        val result = onFindByEmailAuthIdentity?.invoke(email, password)

        mockEmailAuthIdentity[key] = result
        return result
    }

    fun setFindByProviderAuthIdentity(
        type: ProviderAuthentication.Type,
        providerId: String,
        onFindByIdentity: ((ProviderAuthentication.Type, String) -> ProviderAuthentication?)? = null
    ): ProviderAuthentication? {
        if (type == ProviderAuthentication.Type.EMAIL_AND_PASSWORD) {
            throw IllegalArgumentException("mock email 인증은 ${::setFindByEmailAuthIdentity} 를 호출해야 합니다.")
        }

        val key = mockVerifyProviderAuthKey(type, providerId)
        val result = onFindByIdentity?.invoke(type, providerId)

        mockProviderAuthIdentity[key] = result
        return result
    }

    fun clearMocks() {
        mockProviderAuthVerified.clear()
        mockEmailAuthIdentity.clear()
        mockProviderAuthIdentity.clear()
        mockProviderAuthToIdMap.clear()
    }

    override fun verifyProviderAuth(
        type: ProviderAuthentication.Type,
        providerAuthToken: String
    ): ProviderAuthentication {
        val mockEntryKey = mockVerifyProviderAuthKey(type, providerAuthToken)

        return if (mockProviderAuthVerified.containsKey(mockEntryKey)) {
            mockProviderAuthVerified[mockEntryKey]!!.invoke(type, providerAuthToken)
        } else {
            delegate.verifyProviderAuth(type, providerAuthToken)
        }
    }

    override fun findByEmailAuthIdentity(email: String, encodedPassword: String): ProviderAuthentication? {
        val mockEntryKey = mockVerifyEmailAuthKey(email, encodedPassword)

        return if (mockEmailAuthIdentity.containsKey(mockEntryKey)) {
            mockEmailAuthIdentity[mockEntryKey]
        } else {
            delegate.findByEmailAuthIdentity(email, encodedPassword)
        }
    }

    override fun findByProviderAuthIdentity(
        type: ProviderAuthentication.Type,
        providerId: String
    ): ProviderAuthentication? {
        val mockEntryKey = mockVerifyProviderAuthKey(type, providerId)

        return if (mockProviderAuthIdentity.containsKey(mockEntryKey)) {
            mockProviderAuthIdentity[mockEntryKey]
        } else {
            delegate.findByProviderAuthIdentity(type, providerId)
        }
    }

    private fun mockVerifyEmailAuthKey(email: String, password: String): Pair<String, String> = email to password

    private fun mockVerifyProviderAuthKey(
        type: ProviderAuthentication.Type,
        providerAuthToken: String
    ): Pair<ProviderAuthentication.Type, String> = type to providerAuthToken
}
