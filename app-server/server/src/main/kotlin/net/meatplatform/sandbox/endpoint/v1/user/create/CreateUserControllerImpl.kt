/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1.user.create

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.meatplatform.sandbox.domain.auth.usecase.CreateAccessTokenUseCase
import net.meatplatform.sandbox.domain.user.usecase.CreateUserUseCase
import net.meatplatform.sandbox.endpoint.common.JwtTokenIssuerMixin
import net.meatplatform.sandbox.endpoint.v1.user.CreateUserController
import net.meatplatform.sandbox.endpoint.v1.user.common.SimpleUserResponse
import net.meatplatform.sandbox.exception.external.IllegalHttpMessageException
import net.meatplatform.sandbox.util.extractIpStr
import org.springframework.web.bind.annotation.RestController

/**
 * @since 2022-02-14
 */
@RestController
internal class CreateUserControllerImpl(
    private val userBusiness: CreateUserUseCase,
    override val tokenBusiness: CreateAccessTokenUseCase,
    override val objectMapper: ObjectMapper
) : CreateUserController, JwtTokenIssuerMixin {
    override fun create(
        req: CreateUserRequest,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse
    ): SimpleUserResponse {
        if (!req.isAuthenticationFilled) {
            throw IllegalHttpMessageException()
        }

        val createdUser = userBusiness.createUser(req, httpRequest.extractIpStr()).also {
            httpResponse.issueTokenPairsOf(it)
        }

        return SimpleUserResponse.from(createdUser)
    }
}
