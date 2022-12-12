/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.advice.errorhandler

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import net.meatplatform.sandbox.advice.toHttpStatus
import net.meatplatform.sandbox.endpoint.ApiPaths
import net.meatplatform.sandbox.endpoint.v1.ErrorResponseV1
import net.meatplatform.sandbox.endpoint.v1.ResponseEnvelopeV1
import net.meatplatform.sandbox.exception.ErrorCodeBook
import net.meatplatform.sandbox.exception.ExternalException
import net.meatplatform.sandbox.exception.InternalException
import net.meatplatform.sandbox.exception.SandboxException
import net.meatplatform.sandbox.exception.external.MalformedInputException
import net.meatplatform.sandbox.exception.external.WrongInputException
import net.meatplatform.sandbox.i18n.LocaleProvider
import net.meatplatform.sandbox.i18n.MessageTemplateProvider
import net.meatplatform.sandbox.util.originalRequestUri
import net.meatplatform.sandbox.util.toHttpStatus
import org.slf4j.Logger
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConversionException
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpStatusCodeException

/**
 * api-main 앱 내에서 발생하는 모든 예외를 [ErrorResponseV1] 타입으로 변경하는 공통 로직
 *
 * @since 2022-02-14
 */
@RestController
@RestControllerAdvice
class V1ExceptionResponseDecorator(
    private val servletExceptionHandler: V1ExceptionHandlerContract<ServletException>,
    private val localeProvider: LocaleProvider<String?>,
    private val messageTemplateProvider: MessageTemplateProvider,
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

        val httpStatus = req.toHttpStatus()
        if (log.isErrorEnabled) {
            log.error("Unhandled servlet exception: HTTP {} : {}", httpStatus.value(), req.originalRequestUri())
        }

        return postProcessError(req, InternalException(ErrorCodeBook.UNHANDLED_EXCEPTION))
    }

    @ExceptionHandler(Exception::class)
    fun onError(
        req: HttpServletRequest,
        exception: Exception
    ): ResponseEntity<ErrorResponseV1> {
        if (exception is HttpStatusCodeException) {
            log.error("Unhandled http exception: HTTP {} : {}", exception.statusCode, req.originalRequestUri())

            val response = ResponseEnvelopeV1.error(
                exception.message ?: "Unhandled HTTP exception(${exception.statusCode})",
                ErrorCodeBook.UNHANDLED_EXCEPTION.code,
                exception.statusCode
            )

            return ResponseEntity(response, exception.statusCode)
        }

        val sandboxException = dispatchExceptions(req, exception) ?: handleGenericException(exception)

        return postProcessError(req, sandboxException)
    }

    private fun dispatchExceptions(req: HttpServletRequest, exception: Exception): SandboxException? =
        when (exception) {
            is SandboxException -> onSandboxException(exception)
            is BindException -> WrongInputException(
                value = exception.fieldErrors.map { it.field }.toSortedSet().joinToString { it },
                cause = exception
            )

            is HttpMessageConversionException -> MalformedInputException(cause = exception)
            is ServletException -> servletExceptionHandler.onException(req, exception)

            else -> null
        }

    private fun onSandboxException(exception: SandboxException): SandboxException {
        when (exception) {
            is ExternalException -> exception.also { log.debug("Client exception:", it) }
            is InternalException -> exception.also { log.debug("Server exception:", it) }
        }

        return exception
    }

    private fun handleGenericException(exception: Exception): SandboxException {
        log.error("Unhandled exception:", exception)

        return InternalException(codeBook = ErrorCodeBook.UNHANDLED_EXCEPTION, cause = exception)
    }

    /*
     * java Exception 의 getLocalizedMessage 는 인터페이스가 너무 간단해서 외부 커스터마이징이 어렵다.
     * 따라서 로케일에 해당하는 오류 메시지를 직접 생성해야 한다.
     */
    private fun postProcessError(
        req: HttpServletRequest,
        exception: SandboxException?
    ): ResponseEntity<ErrorResponseV1> {
        val message = if (exception == null) {
            DEFAULT_ERROR_MESSAGE
        } else {
            val clientLocale = localeProvider.resolveLocale(req.getHeader(HttpHeaders.ACCEPT_LANGUAGE))
            val messageTemplate = messageTemplateProvider.provide(clientLocale, exception.codeBook)

            exception.messageArguments?.let {
                @Suppress("SpreadOperator")     // vararg 때문에 어쩔 수 없다
                messageTemplate?.format(*it)
            } ?: exception.message
        }
        val (response, httpStatus) = if (exception == null) {
            ResponseEnvelopeV1.error(
                message, ErrorCodeBook.UNHANDLED_EXCEPTION.toString(), null
            ) to HttpStatus.INTERNAL_SERVER_ERROR
        } else {
            ResponseEnvelopeV1.error(
                message, exception.codeBook.code, exception.details
            ) to exception.codeBook.toHttpStatus()
        }

        return ResponseEntity(response, httpStatus)
    }

    companion object {
        private const val SERVLET_EXCEPTION = "javax.servlet.error.exception"
        private const val DEFAULT_ERROR_MESSAGE = "Cannot process this request."
    }
}
