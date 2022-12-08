/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.exception.external

import net.meatplatform.sandbox.exception.ErrorCodeBook
import net.meatplatform.sandbox.exception.ExternalException

/**
 * 지원하지 않는 형식의 결과를 반환하라는 요청을 받았을 때 발생시킬 예외
 *
 * @since 2022-02-14
 */
class WrongPresentationRequestException(
    override val message: String = "Requested data presentation is not supported.",
    override val cause: Throwable? = null
) : ExternalException(ErrorCodeBook.WRONG_PRESENTATION, message, cause)
