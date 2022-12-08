/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.exception.external

import net.meatplatform.sandbox.exception.ErrorCodeBook
import net.meatplatform.sandbox.exception.ExternalException

/**
 * 요청을 처리할 Domain Service 를 찾지 못했을 때 발생시킬 예외
 *
 * @since 2022-02-14
 */
internal class RequestedServiceNotFoundException(
    override val message: String = "Requested service is not found.",
    override val cause: Throwable? = null
) : ExternalException(ErrorCodeBook.SERVICE_NOT_FOUND, message, cause)
