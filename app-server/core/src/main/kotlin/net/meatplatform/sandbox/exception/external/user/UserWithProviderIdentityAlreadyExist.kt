/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.exception.external.user

import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.exception.ErrorCodeBook
import net.meatplatform.sandbox.exception.external.MalformedInputException

/**
 * @since 2022-12-09
 */
class UserWithProviderIdentityAlreadyExist constructor(
    private val providerType: ProviderAuthentication.Type,
    private val providerId: String,
    override val message: String = "User with '$providerId@$providerType' is already registered.",
    override val cause: Throwable? = null
) : MalformedInputException(ErrorCodeBook.USER_ALREADY_REGISTERED, message, cause) {
    override val messageArguments: Array<String> = arrayOf("'$providerId@$providerType'")
}
