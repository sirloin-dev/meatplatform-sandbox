/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.exception.client

import com.sirloin.sandbox.server.core.exception.ClientException
import com.sirloin.sandbox.server.core.exception.MtExceptionCode
import com.sirloin.sandbox.server.core.i18n.LocaleProvider

/**
 * 내용은 해독했지만, 문맥이나 타입에 맞지 않는 입력을 받았을 때 발생시킬 예외
 *
 * @since 2022-02-14
 * @see MalformedInputException
 */
class WrongInputException constructor(
    localeProvider: LocaleProvider,
    value: Any,
    override val cause: Throwable? = null
) : ClientException(localeProvider, MtExceptionCode.WRONG_INPUT, value, cause)
