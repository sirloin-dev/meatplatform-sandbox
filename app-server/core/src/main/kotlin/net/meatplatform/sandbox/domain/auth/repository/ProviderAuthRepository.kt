/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.auth.repository

import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.exception.external.auth.ProviderAuthenticationFailedException
import net.meatplatform.sandbox.exception.external.auth.SuspiciousIpAddressException

/**
 * @since 2022-02-14
 */
interface ProviderAuthRepository {
    /**
     * 제3자 로그인 실패시 [ProviderAuthenticationFailedException] 이 발생합니다.
     */
    fun verifyProviderAuth(type: ProviderAuthentication.Type, providerAuthToken: String): ProviderAuthentication

    fun findByEmailAuthIdentity(email: String, encodedPassword: String): ProviderAuthentication?

    fun findByProviderAuthIdentity(type: ProviderAuthentication.Type, providerId: String): ProviderAuthentication?

    companion object {
        const val NAME = "net.meatplatform.sandbox.domain.auth.ProviderAuthRepository"
    }
}
