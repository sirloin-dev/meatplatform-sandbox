/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.exception.external

import net.meatplatform.sandbox.exception.ErrorCodeBook
import net.meatplatform.sandbox.exception.ExternalException

/**
 * 내용 해독 불가능한 입력을 받았을 때 발생시킬 예외
 *
 * @since 2022-02-14
 */
open class MalformedInputException(
    override val codeBook: ErrorCodeBook = ErrorCodeBook.MALFORMED_INPUT,
    override val message: String = "Malformed input.",
    override val cause: Throwable? = null
) : ExternalException(codeBook, message, cause)
