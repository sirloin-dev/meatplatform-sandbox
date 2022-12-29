/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.exception.internal

import net.meatplatform.sandbox.exception.ErrorCodeBook
import net.meatplatform.sandbox.exception.InternalException

/**
 * @since 2022-12-27
 */
class IpAuthenticationNotFoundException constructor(
    override val message: String = "No IP address authentication is found for this user. This is a server error.",
    override val cause: Throwable? = null
) : InternalException(ErrorCodeBook.UNHANDLED_EXCEPTION, message, cause)
