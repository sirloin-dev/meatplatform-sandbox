/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.exception.external

import net.meatplatform.sandbox.exception.ErrorCodeBook
import net.meatplatform.sandbox.exception.ExternalException

/**
 * @since 2022-02-14
 */
internal class IllegalHttpMessageException(
    override val message: String = "Unparsable HTTP message.",
    override val cause: Throwable? = null
) : ExternalException(ErrorCodeBook.WRONG_INPUT, message, cause)
