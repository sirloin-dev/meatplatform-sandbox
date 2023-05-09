/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.security

import com.sirloin.jvmlib.text.matchesIn
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.RequestDispatcher
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import net.meatplatform.sandbox.advice.errorhandler.V1ExceptionResponseDecorator
import net.meatplatform.sandbox.exception.external.auth.AuthenticationRequiredException
import net.meatplatform.sandbox.security.authentication.HttpAuthenticationScheme
import org.slf4j.Logger
import org.springframework.http.HttpHeaders
import org.springframework.security.web.util.matcher.RequestMatcher
import java.util.regex.Pattern

/**
 * 구현 노트: Spring web 을 쓴다면 Interceptor 방식이 과연 그렇게 큰 성능상의 손해가 있을까?
 * Servlet Filter 는 Spring context 밖에서 동작하기 때문에 여러가지 불편한 점이 한두가지가 아니다.
 * 특히 오류 처리같은 경우 RestControllerAdvice - ExceptionHandler 를 쓸 수 없어서 더욱 그렇다.
 *
 * Servlet Filter 내부에서 발생하는 모든 오류들은 /error endpoint 로 전송된다.
 * 따라서 공통 예외처리를 적용하려면 ErrorController 가 /error 를 핸들링할 수 있도록 구현해야 한다.
 *
 * @since 2022-03-20
 * @see V1ExceptionResponseDecorator
 */
internal class AuthorizationHeaderFilter(
    private val authBypassRequestMatchers: List<RequestMatcher>,
    private val log: Logger
) : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val req = (request as? HttpServletRequest)?.takeIf { r ->
            val result = authBypassRequestMatchers.none { it.matches(r) }

            if (!result) {
                log.trace("Bypassing security check: {} {}", r.method, r.requestURI)
            }

            return@takeIf result
        } ?: run {
            chain.doFilter(request, response)
            return
        }

        // Header 있는지 정도만 검사한다. 토큰 유효성 검사는 AuthenticationProvider 에서 처리한다.
        findAuthorizationHeader(req) ?: run {
            log.trace("No '$HEADER_AUTH' header is found.: {} {}", req.method, req.requestURI)
            val exception = AuthenticationRequiredException()
            req.setAttribute(RequestDispatcher.ERROR_EXCEPTION, exception)
            // 후속 Filter 처리를 중단하려면 예외를 발생시켜야 함
            throw exception
        }

        chain.doFilter(request, response)
    }

    companion object {
        private const val HEADER_AUTH: String = HttpHeaders.AUTHORIZATION

        private val AUTHORIZATION_SYNTAX = Pattern.compile("(?i)(" +
                "${HttpAuthenticationScheme.BASIC.code}|" +
                "${HttpAuthenticationScheme.BEARER.code}|" +
                "${HttpAuthenticationScheme.TOKEN.code}) " +
                "[A-Za-z0-9.+_-]+"
        )

        private fun findAuthorizationHeader(
            req: HttpServletRequest
        ): Pair<HttpAuthenticationScheme, String>? = req.getHeader(HEADER_AUTH)?.run {
            if (matchesIn(AUTHORIZATION_SYNTAX)) {
                split(" ").let {
                    HttpAuthenticationScheme.from(it[0]) to it[1]
                }
            } else {
                null
            }
        }
    }
}
