/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.advice.errorhandler

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import net.meatplatform.sandbox.exception.SandboxException
import net.meatplatform.sandbox.exception.external.IllegalHttpMessageException
import net.meatplatform.sandbox.exception.external.RequestedServiceNotFoundException
import net.meatplatform.sandbox.exception.external.WrongPresentationRequestException
import org.slf4j.Logger
import org.springframework.stereotype.Component
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.NoHandlerFoundException

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
    private val log: Logger
) : V1ExceptionHandlerContract<ServletException> {
    override fun onException(req: HttpServletRequest, exception: ServletException): SandboxException? =
        when (exception) {
            is MissingServletRequestPartException, is ServletRequestBindingException ->
                onBadRequestException(exception)

            is HttpRequestMethodNotSupportedException -> onHttpRequestMethodNotSupportedException(exception)
            is NoHandlerFoundException -> onNoHandlerFoundException(exception)
            is HttpMediaTypeNotSupportedException -> onHttpMediaTypeNotSupportedException(exception)
            else -> null
        }?.let { e ->
            return e.also { log.debug("javax.servlet.ServletException:", it) }
        }

    private fun onBadRequestException(exception: ServletException): SandboxException =
        IllegalHttpMessageException(cause = exception)

    // 원래는 405 지만, 404 로 처리한다.
    private fun onHttpRequestMethodNotSupportedException(
        exception: HttpRequestMethodNotSupportedException
    ): SandboxException = RequestedServiceNotFoundException(cause = exception)

    // 404
    private fun onNoHandlerFoundException(exception: NoHandlerFoundException): SandboxException =
        RequestedServiceNotFoundException(cause = exception)

    // 415
    private fun onHttpMediaTypeNotSupportedException(
        exception: HttpMediaTypeNotSupportedException
    ): SandboxException = WrongPresentationRequestException(cause = exception)
}
