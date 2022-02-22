/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.exception.client

import com.sirloin.sandbox.server.core.exception.ClientException
import com.sirloin.sandbox.server.core.exception.MtExceptionCode
import com.sirloin.sandbox.server.core.i18n.LocaleProvider

/**
 * 요청을 처리할 Domain Service 를 찾지 못했을 때 발생시킬 예외
 *
 * @since 2022-02-14
 */
class RequestedServiceNotFoundException(
    localeProvider: LocaleProvider,
    override val cause: Throwable? = null
) : ClientException(localeProvider, MtExceptionCode.SERVICE_NOT_FOUND, null, cause)
