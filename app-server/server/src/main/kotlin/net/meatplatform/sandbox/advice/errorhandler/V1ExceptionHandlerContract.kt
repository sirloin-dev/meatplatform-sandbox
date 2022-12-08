/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.advice.errorhandler

import jakarta.servlet.http.HttpServletRequest
import net.meatplatform.sandbox.advice.toHttpStatus
import net.meatplatform.sandbox.endpoint.v1.ErrorResponseV1
import net.meatplatform.sandbox.endpoint.v1.ResponseEnvelopeV1
import net.meatplatform.sandbox.exception.SandboxException
import org.springframework.http.ResponseEntity

/**
 * V1 API 들의 공통 예외처리 규칙
 *
 * @since 2022-02-14
 */
interface V1ExceptionHandlerContract<T : Exception> {
    fun onException(req: HttpServletRequest, exception: T): SandboxException?

    fun SandboxException.toResponse(): ResponseEntity<ErrorResponseV1> {
        val response = ResponseEnvelopeV1.error(message, codeBook.code, details)
        val httpStatus = codeBook.toHttpStatus()

        return ResponseEntity(response, httpStatus)
    }
}
