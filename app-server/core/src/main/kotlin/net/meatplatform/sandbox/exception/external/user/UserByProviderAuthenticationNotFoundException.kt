/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.exception.external.user

import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication
import net.meatplatform.sandbox.exception.ErrorCodeBook
import net.meatplatform.sandbox.exception.external.MalformedInputException

/**
 * @since 2023-01-02
 */
class UserByProviderAuthenticationNotFoundException constructor(
    private val providerType: ProviderAuthentication.Type,
    private val providerToken: String,
    override val message: String = "User with '$providerToken' ($providerType) is not found.",
    override val cause: Throwable? = null
) : MalformedInputException(ErrorCodeBook.USER_BY_PROVIDER_AUTH_NOT_FOUND, message, cause) {
    override val messageArguments: Array<String> = arrayOf(providerToken, providerType.name)
}
