package com.sirloin.sandbox.server.api.advice.errorHandler

import com.sirloin.sandbox.server.api.advice.ExceptionCodeToHttpStatusConverter
import com.sirloin.sandbox.server.api.endpoint.v1.ErrorResponseV1
import com.sirloin.sandbox.server.api.endpoint.v1.ResponseEnvelopeV1
import com.sirloin.sandbox.server.core.exception.MtException
import com.sirloin.sandbox.server.core.exception.client.RequestedServiceNotFoundException
import com.sirloin.sandbox.server.core.exception.client.WrongPresentationRequestException
import com.sirloin.sandbox.server.core.i18n.LocaleProvider
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.servlet.NoHandlerFoundException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest

/**
 * V1 API 들의 [ServletException] 처리 규칙
 *
 * ServletException 은 주로 Spring context 바깥에서 발생하는 경우가 많고, 종류가 다양하므로
 * 발견할 때 마다 처리 로직을 여기 추가해 주시기 바랍니다.
 *
 * @since 2022-02-14
 */
@Component
class V1ServletExceptionHandler(
    private val localeProvider: LocaleProvider,
    private val statusConverter: ExceptionCodeToHttpStatusConverter,
    private val log: Logger
) : ApiV1ExceptionHandlerContract<ServletException> {
    override fun onException(req: HttpServletRequest, exception: ServletException): ResponseEntity<ErrorResponseV1>? =
        when (exception) {
            is HttpRequestMethodNotSupportedException -> onHttpRequestMethodNotSupportedException(exception)
            is NoHandlerFoundException -> onNoHandlerFoundException(exception)
            is HttpMediaTypeNotSupportedException -> onHttpMediaTypeNotSupportedException(exception)
            else -> null
        }?.let { (exception, status) ->
            log.debug("javax.servlet.ServletException:", exception)

            val exceptionCode = exception.code
            val httpStatus = statusConverter.convert(exceptionCode) ?: status

            return ResponseEntity(ResponseEnvelopeV1.error(exception), httpStatus)
        }

    // 원래는 405 지만, 404 로 처리한다.
    private fun onHttpRequestMethodNotSupportedException(
        exception: HttpRequestMethodNotSupportedException
    ): Pair<MtException, HttpStatus> =
        RequestedServiceNotFoundException(this.localeProvider, exception) to HttpStatus.NOT_FOUND

    // 404
    private fun onNoHandlerFoundException(exception: NoHandlerFoundException): Pair<MtException, HttpStatus> =
        RequestedServiceNotFoundException(this.localeProvider, exception) to HttpStatus.NOT_FOUND

    // 415
    private fun onHttpMediaTypeNotSupportedException(
        exception: HttpMediaTypeNotSupportedException
    ): Pair<MtException, HttpStatus> =
        WrongPresentationRequestException(this.localeProvider, exception) to HttpStatus.UNSUPPORTED_MEDIA_TYPE
}
