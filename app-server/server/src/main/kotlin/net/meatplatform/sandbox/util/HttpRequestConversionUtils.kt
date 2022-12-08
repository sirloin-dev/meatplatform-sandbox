/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.util

import net.meatplatform.sandbox.exception.external.WrongInputException
import org.slf4j.Logger
import java.util.*

fun uuidStringToUuid(uuidStr: String, log: Logger? = null): UUID = try {
    UUID.fromString(uuidStr)
} catch (e: IllegalArgumentException) {
    log?.debug("Not a uuid: {}", uuidStr)
    throw WrongInputException(value = uuidStr, cause = e)
}
