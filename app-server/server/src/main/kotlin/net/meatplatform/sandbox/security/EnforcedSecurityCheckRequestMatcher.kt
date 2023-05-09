/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.security

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.web.util.matcher.RequestMatcher

/**
 * 요청이 Servlet Filter(Spring Security)의 대상인지 여부를 판단하는 로직
 *
 * @since 2022-03-20
 */
class EnforcedSecurityCheckRequestMatcher(
    private val bypassMatchers: List<RequestMatcher>
) : RequestMatcher {
    override fun matches(request: HttpServletRequest?): Boolean {
        bypassMatchers.forEach {
            // Bypass 대상과 일치하니까, Servlet Filter 처리를 하지 않도록 false 를 반환
            if (it.matches(request)) {
                return false
            }
        }

        // 요청이 Bypass 대상과 일치하지 않으므로 true. Security 설정을 추가 진행.
        return true
    }
}
