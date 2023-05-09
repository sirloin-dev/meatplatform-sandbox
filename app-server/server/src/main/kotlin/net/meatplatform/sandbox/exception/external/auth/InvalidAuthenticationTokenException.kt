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
class InvalidAuthenticationTokenException(
    override val message: String = "Invalid Access Token.",
    override val cause: Throwable? = null
) : ExternalException(ErrorCodeBook.MALFORMED_INPUT, message, cause)
