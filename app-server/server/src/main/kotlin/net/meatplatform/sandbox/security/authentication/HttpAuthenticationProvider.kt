/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.security.authentication

import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.web.client.HttpClientErrorException

/**
 * @since 2022-03-20
 */
class HttpAuthenticationProvider(
    private val tokenVerifier: AuthenticationTokenVerifier
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication?): Authentication {
        val httpAuthToken =
            authentication as? HttpAuthenticationToken ?: throw HttpClientErrorException(HttpStatus.UNAUTHORIZED)

        // We don't need to set this object into SecurityContextHolder, because Spring will do it for us.
        return when (httpAuthToken.principal) {
            HttpAuthenticationScheme.BEARER -> tokenVerifier.verify(httpAuthToken)

            // 지원하지 않는 인증 체계 - 서버 오류인것 처럼 보여주기 싫음.
            else -> throw HttpClientErrorException(HttpStatus.PRECONDITION_FAILED)
        }
    }

    override fun supports(authentication: Class<*>?): Boolean = authentication?.let {
        HttpAuthenticationToken::class.java.isAssignableFrom(it)
    } ?: false
}
