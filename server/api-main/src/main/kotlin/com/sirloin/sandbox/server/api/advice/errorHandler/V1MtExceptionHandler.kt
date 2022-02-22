/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.advice.errorHandler

import com.sirloin.sandbox.server.api.advice.ExceptionCodeToHttpStatusConverter
import com.sirloin.sandbox.server.api.endpoint.v1.ErrorResponseV1
import com.sirloin.sandbox.server.api.endpoint.v1.ResponseEnvelopeV1
import com.sirloin.sandbox.server.core.exception.ClientException
import com.sirloin.sandbox.server.core.exception.MtException
import com.sirloin.sandbox.server.core.exception.ServerException
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

/**
 * V1 API 들의 [MtException] 처리 규칙
 *
 * @since 2022-02-14
 */
@Component
class V1MtExceptionHandler(
    private val statusConverter: ExceptionCodeToHttpStatusConverter,
    private val log: Logger
) : ApiV1ExceptionHandlerContract<MtException> {
    override fun onException(req: HttpServletRequest, exception: MtException): ResponseEntity<ErrorResponseV1>? =
        when (exception) {
            is ClientException -> onClientException(exception)
            is ServerException -> onServerException(exception)
            // POINT: 이 else branch 를 없애려면 어떻게 해야 할까요?
            else -> null
        }

    private fun onClientException(exception: ClientException): ResponseEntity<ErrorResponseV1> {
        log.debug("Client exception:", exception)

        val response = ResponseEnvelopeV1.error(exception)
        val httpStatus = statusConverter.convert(exception.code) ?: HttpStatus.BAD_REQUEST

        return ResponseEntity(response, httpStatus)
    }

    private fun onServerException(exception: ServerException): ResponseEntity<ErrorResponseV1> {
        log.debug("Server exception:", exception)

        val response = ResponseEnvelopeV1.error(exception)
        val httpStatus = statusConverter.convert(exception.code) ?: HttpStatus.INTERNAL_SERVER_ERROR

        return ResponseEntity(response, httpStatus)
    }
}
