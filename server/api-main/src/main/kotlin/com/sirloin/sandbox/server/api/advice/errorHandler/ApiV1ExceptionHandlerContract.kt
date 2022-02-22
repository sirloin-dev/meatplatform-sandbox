/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.advice.errorHandler

import com.sirloin.sandbox.server.api.endpoint.v1.ErrorResponseV1
import org.springframework.http.ResponseEntity
import javax.servlet.http.HttpServletRequest

/**
 * V1 API 들의 공통 예외처리 규칙
 *
 * @since 2022-02-14
 */
interface ApiV1ExceptionHandlerContract<T : Exception> {
    fun onException(req: HttpServletRequest, exception: T): ResponseEntity<ErrorResponseV1>?
}
