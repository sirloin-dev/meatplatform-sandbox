/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.advice.responseDecorator

import com.sirloin.sandbox.server.api.advice.errorHandler.ApiV1ExceptionHandlerContract
import com.sirloin.sandbox.server.api.endpoint.ApiPaths
import com.sirloin.sandbox.server.api.endpoint.v1.ErrorResponseV1
import com.sirloin.sandbox.server.api.endpoint.v1.ResponseEnvelopeV1
import com.sirloin.sandbox.server.core.exception.MtException
import com.sirloin.sandbox.server.core.exception.MtExceptionCode
import com.sirloin.sandbox.server.core.exception.ServerException
import com.sirloin.sandbox.server.core.exception.client.MalformedInputException
import com.sirloin.sandbox.server.core.exception.client.WrongInputException
import com.sirloin.sandbox.server.core.exception.server.UnhandledException
import com.sirloin.sandbox.server.core.i18n.LocaleProvider
import org.slf4j.Logger
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConversionException
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest

/**
 * api-main 앱 내에서 발생하는 모든 예외를 [ErrorResponseV1] 타입으로 변경하는 공통 로직
 *
 * @since 2022-02-14
 */
@RestController
@RestControllerAdvice
class V1ExceptionResponseDecorator(
    private val mtExceptionHandler: ApiV1ExceptionHandlerContract<MtException>,
    private val servletExceptionHandler: ApiV1ExceptionHandlerContract<ServletException>,
    private val localeProvider: LocaleProvider,
    private val log: Logger
) : ErrorController {
    /**
     * Spring context 바깥의 로직(특히 서블릿 필터)에서 발생한 오류는 DispatcherServlet 를 타지 않는다.
     * 이 때문에 오류 처리의 일관성이 없어지므로 이를 해결하고자 "/error" 경로를 재정의해 오류를 ErrorResponseV1 형태로
     * 감싼다.
     *
     * 주의: V2 등의 Endpoint 가 생길 경우 별도 분기 처리 필요.
     */
    @RequestMapping(ApiPaths.ERROR)
    fun handleError(req: HttpServletRequest): ResponseEntity<ErrorResponseV1> {
        (req.getAttribute(SERVLET_EXCEPTION) as? Exception)?.let { return onError(req, it) }

        // POINT: 다른곳과 달리 여기서 log 가능여부를 굳이 체크하는 이유가 뭘까요?
        if (log.isErrorEnabled) {
            log.error("Unhandled servlet exception: {}", "${req.requestURI}?${req.queryString}")
        }

        val response = ResponseEnvelopeV1.error(
            ServerException(this.localeProvider, MtExceptionCode.UNHANDLED_EXCEPTION)
        )

        return ResponseEntity(response, req.toHttpStatus())
    }

    @ExceptionHandler(Exception::class)
    fun onError(
        req: HttpServletRequest,
        exception: Exception
    ): ResponseEntity<ErrorResponseV1> {
        return dispatchExceptions(req, exception) ?: handleGenericException(exception)
    }

    private fun dispatchExceptions(req: HttpServletRequest, exception: Exception): ResponseEntity<ErrorResponseV1>? =
        when (exception) {
            is MtException -> mtExceptionHandler.onException(req, exception)
            is ServletException -> servletExceptionHandler.onException(req, exception)
            is BindException -> mtExceptionHandler.onException(
                req,
                WrongInputException(this.localeProvider, exception.fieldErrors.joinToString { it.field }, exception)
            )
            is HttpMessageConversionException -> mtExceptionHandler.onException(
                req, MalformedInputException(this.localeProvider, exception)
            )
            else -> null
        }

    private fun handleGenericException(exception: Exception): ResponseEntity<ErrorResponseV1> {
        log.error("Unhandled exception:", exception)

        val response = ResponseEnvelopeV1.error(UnhandledException(this.localeProvider, exception))

        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun HttpServletRequest.toHttpStatus(): HttpStatus {
        (getAttribute(SERVLET_STATUS_CODE) as? Int)?.let {
            return HttpStatus.valueOf(it)
        }

        return HttpStatus.INTERNAL_SERVER_ERROR
    }

    companion object {
        private const val SERVLET_EXCEPTION = "javax.servlet.error.exception"
        private const val SERVLET_STATUS_CODE = "javax.servlet.error.status_code"
    }
}
