/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1.user

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import net.meatplatform.sandbox.endpoint.v1.ApiPathsV1
import net.meatplatform.sandbox.endpoint.v1.auth.LoginController.Companion.HEADER_AUTHORIZATION
import net.meatplatform.sandbox.endpoint.v1.auth.LoginController.Companion.HEADER_X_AUTHORIZATION_RESPONSE
import net.meatplatform.sandbox.endpoint.v1.user.common.SimpleUserResponse
import net.meatplatform.sandbox.endpoint.v1.user.create.CreateUserRequest
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * ```
 * POST /user
 *
 * Content-Type: application/json
 * ```
 *
 * 이용자 생성 성공시 Access Token 과 Refresh Token 을 Header 로 내려줍니다. Header 의 이름은 각각
 * [HEADER_AUTHORIZATION] 과 [HEADER_X_AUTHORIZATION_RESPONSE] 입니다.
 *
 * @since 2022-02-14
 */
@RequestMapping(
    produces = [MediaType.APPLICATION_JSON_VALUE],
    consumes = [MediaType.APPLICATION_JSON_VALUE]
)
interface CreateUserController {
    @RequestMapping(
        path = [ApiPathsV1.USER],
        method = [RequestMethod.POST]
    )
    fun create(
        @Valid @RequestBody req: CreateUserRequest,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse
    ): SimpleUserResponse
}
