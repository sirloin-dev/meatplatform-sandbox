/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.exception.client

import com.sirloin.sandbox.server.core.exception.ClientException
import com.sirloin.sandbox.server.core.exception.MtExceptionCode
import com.sirloin.sandbox.server.core.i18n.LocaleProvider

/**
 * 지원하지 않는 형식의 결과를 반환하라는 요청을 받았을 때 발생시킬 예외
 *
 * @since 2022-02-14
 */
class WrongPresentationRequestException(
    localeProvider: LocaleProvider,
    override val cause: Throwable? = null
) : ClientException(localeProvider, MtExceptionCode.WRONG_PRESENTATION, null, cause)
