/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.domain.repository.auth

import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.auth.repository.ProviderAuthRepository
import test.domain.usecase.auth.random

/**
 * @since 2022-02-14
 */
class SpyProviderAuthRepository(
    private val delegate: ProviderAuthRepository
) : ProviderAuthRepository {
    private val mockProviderAuthVerified = HashMap<Pair<ProviderAuthentication.Type, String>, ProviderAuthentication>()
    private val mockEmailAuthIdentity = HashMap<Pair<String, String>, ProviderAuthentication?>()
    private val mockProviderAuthIdentity = HashMap<Pair<ProviderAuthentication.Type, String>, ProviderAuthentication?>()

    fun setProviderAuthVerified(
        type: ProviderAuthentication.Type?,
        providerAuthToken: String?,
        onVerified: ((ProviderAuthentication.Type, String) -> ProviderAuthentication)? = null
    ): ProviderAuthentication {
        if (type == null || providerAuthToken == null) {
            throw IllegalArgumentException(
                "ProviderAuthentication, ProviderAuthToken 은 모두 null 이 아니어야 합니다" +
                        "(ProviderAuthentication=$type, ProviderAuthToken=$providerAuthToken)."
            )
        }

        val key = mockVerifyProviderAuthKey(type, providerAuthToken)
        val result = onVerified?.invoke(type, providerAuthToken) ?: ProviderAuthentication.random(type = type)

        mockProviderAuthVerified[key] = result
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
    }

    override fun verifyProviderAuth(
        type: ProviderAuthentication.Type,
        providerAuthToken: String
    ): ProviderAuthentication {
        val mockEntryKey = mockVerifyProviderAuthKey(type, providerAuthToken)

        return if (mockProviderAuthVerified.containsKey(mockEntryKey)) {
            mockProviderAuthVerified[mockEntryKey]!!
        } else {
            delegate.verifyProviderAuth(type, providerAuthToken)
        }
    }

    override fun findByEmailAuthIdentity(email: String, password: String): ProviderAuthentication? {
        val mockEntryKey = mockVerifyEmailAuthKey(email, password)

        return if (mockEmailAuthIdentity.containsKey(mockEntryKey)) {
            mockEmailAuthIdentity[mockEntryKey]
        } else {
            delegate.findByEmailAuthIdentity(email, password)
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
