/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.advice

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.meatplatform.sandbox.appconfig.ClientVersionConfigHolder
import net.meatplatform.sandbox.exception.external.UnsupportedClientVersionException
import net.meatplatform.sandbox.security.authentication.VerifiedAuthentication
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

/**
 * 이 코드는 사전에 [VerifiedAuthentication] 을 반환하는 [AuthenticationManager] 를
 * Spring Security 에 설정했다는 가정 하에 동작합니다.
 *
 * 만약 현재 요청내에 인증 정보가 있다면 그 정보에 User Agent 를 파싱한 결과를 추가합니다.
 *
 * @since 2022-12-07
 */
@Component
internal class UserAgentParserInterceptor(
    private val clientVersionConfig: ClientVersionConfigHolder
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val clientInfo = request.getHeader("User-Agent")?.let { UserAgentParser.toClientInfo(it) }
        val clientVersion = clientInfo?.appVersion

        with(clientVersionConfig) {
            if (clientVersion == null || clientVersion < minRequiredVersion) {
                throw UnsupportedClientVersionException(
                    requiredVersion = minRequiredVersion,
                    updateUri = clientInfo?.devicePlatform?.let { getUpdateUriOf(it) } ?: defaultWebUrl
                )
            }
        }

        with(SecurityContextHolder.getContext()) {
            authentication?.let {
                if (it is VerifiedAuthentication) {
                    this.authentication = it.copy(clientInfo = clientInfo)
                }
            }
        }

        return true
    }
}
