/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.exception.external.auth

import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.exception.ErrorCodeBook
import net.meatplatform.sandbox.exception.external.WrongInputException

/**
 * @since 2023-05-06
 */
class ProviderAuthenticationFailedException constructor(
    private val providerType: ProviderAuthentication.Type,
    private val providerToken: String,
    override val message: String = "$providerType(token=$providerToken) verification is failed.",
    override val cause: Throwable? = null
) : WrongInputException(ErrorCodeBook.EXTERNAL_PROVIDER_AUTH_VERIFICATION_FAILED, message, cause) {
    override val messageArguments: Array<String> = arrayOf(providerToken, providerType.name)
}
