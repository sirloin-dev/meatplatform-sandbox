/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.exception.external.auth

import net.meatplatform.sandbox.exception.ErrorCodeBook
import net.meatplatform.sandbox.exception.ExternalException

/**
 * @since 2023-05-09
 */
class AuthenticationExpiredException(
    override val message: String = "Authentication is expired.",
    override val cause: Throwable? = null
) : ExternalException(ErrorCodeBook.AUTHENTICATION_EXPIRED, message, cause)
