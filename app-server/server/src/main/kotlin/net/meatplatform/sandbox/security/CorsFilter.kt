/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.meatplatform.sandbox.AppProfile
import net.meatplatform.sandbox.SandboxApplication
import org.springframework.web.filter.OncePerRequestFilter

/**
 * @since 2022-05-13
 */
class CorsFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        /*
         * 보안 확실하게 하려면 도메인 이름을 명시하는게 좋다. 하지만 웹 클라 개발자가 localhost 환경에서
         * 특정 환경에 접근한다던지 하는 시나리오에서 불편함이 있을 수 있으니 * 로 허용한다.
         */
        val allowedOrigin = when (SandboxApplication.buildConfig.profile) {
            AppProfile.LOCAL -> "*"
            AppProfile.ALPHA -> "*"
            AppProfile.BETA -> "*"      // Domain name 확정 이후 변경 필요
            AppProfile.RELEASE -> "*"   // Domain name 확정 이후 변경 필요
        }

        response.apply {
            setHeader("Access-Control-Allow-Origin", allowedOrigin)
            setHeader("Access-Control-Allow-Methods", "*")
            setHeader("Access-Control-Max-Age", "3600")
            setHeader("Access-Control-Allow-Headers", "*")
        }

        // CORS Preflight 응답인 경우 filter 진행시키지 않고 즉시 종료
        if ("OPTIONS" == request.method && request.getHeader("Sec-Fetch-Mode") == "cors") {
            response.status = HttpServletResponse.SC_OK
        } else {
            filterChain.doFilter(request, response)
        }
    }
}
