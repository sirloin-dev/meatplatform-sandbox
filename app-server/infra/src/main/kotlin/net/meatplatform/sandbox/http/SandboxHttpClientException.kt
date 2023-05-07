/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.http

import net.meatplatform.sandbox.exception.ErrorCodeBook
import net.meatplatform.sandbox.exception.InternalException
import java.net.URI

/**
 * @since 2023-05-07
 */
class SandboxHttpClientException (
    private val method: String,
    private val uri: URI,
    override val message: String = "HTTP $method $uri call was failed.",
    override val cause: Throwable? = null
) : InternalException(ErrorCodeBook.EXTERNAL_SYSTEM_NOT_RESPONDING, message, cause)
