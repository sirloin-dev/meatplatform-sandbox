/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.exception.server

import com.sirloin.sandbox.server.core.exception.MtExceptionCode
import com.sirloin.sandbox.server.core.exception.ServerException
import com.sirloin.sandbox.server.core.i18n.LocaleProvider

/**
 * 미처 try - catch 로 처리하지 못한 상황에서 발생시킬 예외.
 * 개발자의 실수로 발생할 확률이 높으므로 이 예외 상황을 발견한다면 재현경로를 최대한 빨리 찾고, 문제를 수정해야 합니다.
 *
 * @since 2022-02-14
 */
class UnhandledException(
    localeProvider: LocaleProvider,
    override val cause: Throwable? = null
) : ServerException(localeProvider, MtExceptionCode.UNHANDLED_EXCEPTION, null, cause)
