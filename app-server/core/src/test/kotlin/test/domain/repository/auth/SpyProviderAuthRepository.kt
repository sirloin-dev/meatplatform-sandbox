/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.domain.repository.auth

import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.repository.auth.ProviderAuthRepository
import test.domain.usecase.auth.random

/**
 * @since 2022-02-14
 */
class SpyProviderAuthRepository(
    private val delegate: ProviderAuthRepository
) : ProviderAuthRepository {
    private val mockProviderAuthVerified = HashMap<Pair<ProviderAuthentication.Type, String>, ProviderAuthentication>()
    private val mockIdentity = HashMap<Pair<ProviderAuthentication.Type, String>, ProviderAuthentication?>()

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

    fun setIdentity(
        type: ProviderAuthentication.Type?,
        providerId: String?,
        onFindByIdentity: ((ProviderAuthentication.Type, String) -> ProviderAuthentication?)? = null
    ): ProviderAuthentication? {
        if (type == null || providerId == null) {
            throw IllegalArgumentException(
                "ProviderAuthentication, ProviderId 는 모두 null 이 아니어야 합니다" +
                        "(ProviderAuthentication=$type, ProviderId=$providerId)."
            )
        }

        val key = mockVerifyProviderAuthKey(type, providerId)
        val result = onFindByIdentity?.invoke(type, providerId)

        mockIdentity[key] = result
        return result
    }

    fun clearMocks() {
        mockProviderAuthVerified.clear()
        mockIdentity.clear()
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

    override fun findByIdentity(type: ProviderAuthentication.Type, providerId: String): ProviderAuthentication? {
        val mockEntryKey = mockVerifyProviderAuthKey(type, providerId)

        return if (mockIdentity.containsKey(mockEntryKey)) {
            mockIdentity[mockEntryKey]
        } else {
            delegate.verifyProviderAuth(type, providerId)
        }
    }

    private fun mockVerifyProviderAuthKey(
        type: ProviderAuthentication.Type,
        providerAuthToken: String
    ): Pair<ProviderAuthentication.Type, String> = type to providerAuthToken
}
