/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.advice.web

import net.meatplatform.sandbox.exception.ErrorCodeBook
import org.springframework.http.HttpStatus

/**
 * @since 2022-02-14
 */
fun ErrorCodeBook.toHttpStatus(): HttpStatus = when(this) {
    ErrorCodeBook.UNSUPPORTED_CLIENT_VERSION -> HttpStatus.UPGRADE_REQUIRED
}
