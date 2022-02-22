/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.exception.client

import com.sirloin.sandbox.server.core.exception.ClientException
import com.sirloin.sandbox.server.core.exception.MtExceptionCode
import com.sirloin.sandbox.server.core.i18n.LocaleProvider

/**
 * 내용 해독 불가능한 입력을 받았을 때 발생시킬 예외
 *
 * @since 2022-02-14
 * @see WrongInputException
 */
class MalformedInputException(
    localeProvider: LocaleProvider,
    override val cause: Throwable? = null
) : ClientException(localeProvider, MtExceptionCode.MALFORMED_INPUT, null, cause)
