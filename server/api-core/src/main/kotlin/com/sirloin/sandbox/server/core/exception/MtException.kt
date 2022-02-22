/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.exception

import com.sirloin.sandbox.server.core.i18n.LocaleProvider
import com.sirloin.sandbox.server.core.i18n.MessageProvider

/**
 * sandbox-server 에서 발생시키는 모든 예외의 최상위 타입입니다.
 *
 * @since 2022-02-14
 */
open class MtException protected constructor(
    val localeProvider: LocaleProvider,
    val code: MtExceptionCode,
    details: Any? = null,
    override val cause: Throwable? = null,
) : RuntimeException(
    MessageProvider.hardcodedInstance().provide(
        /*          ^^^^
         * POINT: 여기서 주입하는 MessageProvider 는 하드코딩된 메시지를 가지고 있습니다.
         * 이 구현의 문제점을 진단하고, 개선해 보시기 바랍니다.
         */
        localeProvider.locale,
        code.msgKey,
        details,
    ),
    cause
)
