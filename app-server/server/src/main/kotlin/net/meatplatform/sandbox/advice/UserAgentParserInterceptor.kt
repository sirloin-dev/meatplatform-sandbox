/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.advice

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.meatplatform.sandbox.appconfig.ClientVersionConfigHolder
import net.meatplatform.sandbox.exception.external.UnsupportedClientVersionException
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

/**
 * @since 2022-12-07
 */
@Component
internal class UserAgentParserInterceptor(
    private val clientVersionConfig: ClientVersionConfigHolder
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val clientInfo = request.getHeader("User-Agent")?.let { UserAgentParser.toClientInfo(it) }
        request.setAttribute(REQUEST_ATTR_CLIENT_INFO, clientInfo)
        val clientVersion = clientInfo?.appVersion

        with(clientVersionConfig) {
            if (clientVersion == null || clientVersion < minRequiredVersion) {
                throw UnsupportedClientVersionException(
                    requiredVersion = minRequiredVersion,
                    updateUri = clientInfo?.devicePlatform?.let { getUpdateUriOf(it) } ?: defaultWebUrl
                )
            }
        }

        return true
    }

    companion object {
        const val REQUEST_ATTR_CLIENT_INFO = "net.meatplatform.clientInfo"
    }
}
