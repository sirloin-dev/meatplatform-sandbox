/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.exception

import com.sirloin.sandbox.server.core.i18n.LocaleProvider

/**
 * 서비스 내부의 문제로 인해 요청을 처리할 수 없을 때 발생시키는 예외의 상위 타입입니다.
 *
 * @since 2022-02-14
 */
open class ServerException constructor(
    localeProvider: LocaleProvider,
    code: MtExceptionCode,
    details: Any? = null,
    override val cause: Throwable? = null
) : MtException(localeProvider, code, details, cause)
