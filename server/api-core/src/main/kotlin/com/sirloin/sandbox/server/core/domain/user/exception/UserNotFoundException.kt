/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.domain.user.exception

import com.sirloin.sandbox.server.core.exception.ClientException
import com.sirloin.sandbox.server.core.exception.MtExceptionCode
import com.sirloin.sandbox.server.core.i18n.LocaleProvider

/**
 * User 도메인 모델이 필요하지만, 없는 상황에 발생하는 예외.
 *
 * @since 2022-02-14
 */
class UserNotFoundException constructor(
    localeProvider: LocaleProvider,
    parameter: Any? = null,
    override val cause: Throwable? = null
) : ClientException(localeProvider, MtExceptionCode.USER_NOT_FOUND, parameter, cause)
