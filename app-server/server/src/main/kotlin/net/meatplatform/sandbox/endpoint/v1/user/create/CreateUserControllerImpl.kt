/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1.user.create

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.meatplatform.sandbox.domain.usecase.auth.CreateAccessTokenUseCase
import net.meatplatform.sandbox.domain.usecase.user.CreateUserUseCase
import net.meatplatform.sandbox.endpoint.v1.auth.IssueTokenController.Companion.HEADER_AUTHORIZATION
import net.meatplatform.sandbox.endpoint.v1.auth.IssueTokenController.Companion.HEADER_X_AUTHORIZATION_RESPONSE
import net.meatplatform.sandbox.endpoint.v1.user.CreateUserController
import net.meatplatform.sandbox.endpoint.v1.user.common.SimpleUserResponse
import net.meatplatform.sandbox.exception.external.IllegalHttpMessageException
import net.meatplatform.sandbox.util.JwtTokenBakerMixin
import net.meatplatform.sandbox.util.extractIpStr
import org.springframework.web.bind.annotation.RestController

/**
 * @since 2022-02-14
 */
@RestController
internal class CreateUserControllerImpl(
    private val userBusiness: CreateUserUseCase,
    private val tokenBusiness: CreateAccessTokenUseCase,
    private val objectMapper: ObjectMapper
) : CreateUserController, JwtTokenBakerMixin {
    override fun create(
        req: CreateUserRequest,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse
    ): SimpleUserResponse {
        if (!req.isAuthenticationFilled) {
            throw IllegalHttpMessageException()
        }

        // region TO-DO-20221225: 이 구간 Infrastructure 에 무관하게 Transaction 처리해야 합니다.
        val createdUser = userBusiness.createUser(req, httpRequest.extractIpStr())
        val (accessToken, refreshToken) = tokenBusiness.createTokenOf(createdUser)
        // endregion

        with(httpResponse) {
            setHeader(HEADER_AUTHORIZATION, accessToken.toJwtTokenBy(objectMapper))
            setHeader(HEADER_X_AUTHORIZATION_RESPONSE, refreshToken.toJwtTokenBy(objectMapper))
        }

        return SimpleUserResponse.from(createdUser)
    }
}
