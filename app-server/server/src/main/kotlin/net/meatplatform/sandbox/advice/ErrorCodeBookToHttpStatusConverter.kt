/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.advice

import net.meatplatform.sandbox.exception.ErrorCodeBook
import org.springframework.http.HttpStatus

/**
 * @since 2022-02-14
 */
fun ErrorCodeBook.toHttpStatus(): HttpStatus = when(this) {
    ErrorCodeBook.SERVICE_NOT_FOUND -> HttpStatus.NOT_FOUND
    ErrorCodeBook.WRONG_PRESENTATION -> HttpStatus.UNSUPPORTED_MEDIA_TYPE
    ErrorCodeBook.WRONG_INPUT -> HttpStatus.BAD_REQUEST
    ErrorCodeBook.MALFORMED_INPUT -> HttpStatus.BAD_REQUEST
    ErrorCodeBook.UNSUPPORTED_CLIENT_VERSION -> HttpStatus.UPGRADE_REQUIRED
    ErrorCodeBook.EXTERNAL_SYSTEM_NOT_RESPONDING -> HttpStatus.BAD_GATEWAY
    ErrorCodeBook.USER_ALREADY_REGISTERED -> HttpStatus.CONFLICT
    ErrorCodeBook.USER_BY_PROVIDER_AUTH_NOT_FOUND -> HttpStatus.UNAUTHORIZED
    ErrorCodeBook.EXTERNAL_PROVIDER_AUTH_VERIFICATION_FAILED -> HttpStatus.UNAUTHORIZED
    ErrorCodeBook.SUSPICIOUS_IP_ADDRESS_DETECTED -> HttpStatus.FORBIDDEN
    ErrorCodeBook.STARTUP_FAILED -> HttpStatus.SERVICE_UNAVAILABLE
    ErrorCodeBook.UNHANDLED_EXCEPTION -> HttpStatus.INTERNAL_SERVER_ERROR
}
