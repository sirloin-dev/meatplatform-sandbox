/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.exception

import com.sirloin.sandbox.server.core.i18n.LocaleProvider

/**
 * 사용측의 잘못되거나, 지원할 수 없는 요청 등을 받았을 때 발생시킬 예외의 상위 타입입니다.
 *
 * @since 2022-02-14
 */
open class ClientException constructor(
    localeProvider: LocaleProvider,
    code: MtExceptionCode,
    details: Any? = null,
    override val cause: Throwable? = null
) : MtException(localeProvider, code, details, cause)
