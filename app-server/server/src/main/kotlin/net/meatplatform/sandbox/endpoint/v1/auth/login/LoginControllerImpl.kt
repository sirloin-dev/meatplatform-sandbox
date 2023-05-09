/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1.auth.login

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.meatplatform.sandbox.domain.auth.usecase.CreateAccessTokenUseCase
import net.meatplatform.sandbox.domain.auth.usecase.LoginUseCase
import net.meatplatform.sandbox.security.authentication.JwtTokenIssuerMixin
import net.meatplatform.sandbox.endpoint.common.response.SimpleValueResponse
import net.meatplatform.sandbox.endpoint.v1.auth.LoginController
import net.meatplatform.sandbox.util.extractIpStr
import org.springframework.web.bind.annotation.RestController

/**
 * @since 2022-12-09
 */
@RestController
internal class LoginControllerImpl(
    private val userBusiness: LoginUseCase,
    override val tokenBusiness: CreateAccessTokenUseCase,
    override val objectMapper: ObjectMapper
) : LoginController, JwtTokenIssuerMixin {
    override fun loginByProviderAuth(
        req: LoginRequest,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse
    ): SimpleValueResponse<Boolean> {
        userBusiness.getUserByProviderAuthentication(req.toLoginMessage(), httpRequest.extractIpStr()).also {
            httpResponse.issueTokenPairsOf(it)
        }

        return SimpleValueResponse(true)
    }
}
