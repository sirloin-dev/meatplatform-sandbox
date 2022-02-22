/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.endpoint.util

/**
 * HTTP Message 처리에 공통으로 활용할 로직 모음입니다.
 *
 * @since 2022-02-14
 */
import com.sirloin.sandbox.server.core.exception.client.WrongInputException
import com.sirloin.sandbox.server.core.i18n.LocaleProvider
import org.slf4j.Logger
import java.util.*

fun uuidStringToUuid(uuidStr: String, localeProvider: LocaleProvider, log: Logger? = null): UUID = try {
    UUID.fromString(uuidStr)
} catch (e: IllegalArgumentException) {
    log?.debug("Not a uuid: {}", uuidStr)
    throw WrongInputException(localeProvider, uuidStr, e)
}
