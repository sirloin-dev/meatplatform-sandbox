/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import net.meatplatform.sandbox.endpoint.common.response.SimpleValueResponse
import net.meatplatform.sandbox.endpoint.v1.ApiPathsV1
import net.meatplatform.sandbox.endpoint.v1.auth.login.LoginRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * @since 2022-12-09
 */
@RequestMapping(
    produces = [MediaType.APPLICATION_JSON_VALUE],
    consumes = [MediaType.APPLICATION_JSON_VALUE]
)
interface LoginController {
    @RequestMapping(
        path = [ApiPathsV1.AUTH_LOGIN],
        method = [RequestMethod.POST]
    )
    fun loginByProviderAuth(
        @Valid @RequestBody req: LoginRequest,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse
    ): SimpleValueResponse<Boolean>

    companion object {
        const val HEADER_AUTHORIZATION = HttpHeaders.AUTHORIZATION
        const val HEADER_X_AUTHORIZATION_RESPONSE = "X-Authorization-RefreshToken"
    }
}
