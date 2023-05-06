/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.exception.external

import net.meatplatform.sandbox.exception.ErrorCodeBook

/**
 * 내용은 해독했지만, 문맥이나 타입에 맞지 않는 입력을 받았을 때 발생시킬 예외
 *
 * @since 2022-02-14
 * @see MalformedInputException
 */
open class WrongInputException constructor(
    private val value: Any,
    override val message: String = "'$value' is not a valid input value.",
    override val cause: Throwable? = null
) : MalformedInputException(ErrorCodeBook.WRONG_INPUT, message, cause) {
    override val messageArguments: Array<String> = arrayOf(value.toString())
}
