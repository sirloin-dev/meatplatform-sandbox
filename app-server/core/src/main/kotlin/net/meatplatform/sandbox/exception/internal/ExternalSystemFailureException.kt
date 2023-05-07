/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.exception.internal

import net.meatplatform.sandbox.exception.ErrorCodeBook
import net.meatplatform.sandbox.exception.InternalException
import java.net.URI

/**
 * @since 2023-05-07
 */
class ExternalSystemFailureException (
    override val message: String = "External System is not responding.",
    override val cause: Throwable? = null
) : InternalException(ErrorCodeBook.EXTERNAL_SYSTEM_NOT_RESPONDING, message, cause)
