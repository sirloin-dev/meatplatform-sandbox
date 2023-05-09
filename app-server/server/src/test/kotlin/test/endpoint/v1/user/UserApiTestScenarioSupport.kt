/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.endpoint.v1.user

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldNotBe
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.endpoint.v1.ApiPathsV1
import net.meatplatform.sandbox.endpoint.v1.auth.LoginController
import net.meatplatform.sandbox.endpoint.v1.auth.common.AuthenticationTypeDto
import net.meatplatform.sandbox.endpoint.v1.auth.common.ProviderAuthenticationResponse
import net.meatplatform.sandbox.endpoint.v1.user.common.SimpleUserResponse
import net.meatplatform.sandbox.endpoint.v1.user.create.CreateUserRequest
import net.meatplatform.sandbox.exception.ErrorCodeBook
import org.springframework.http.HttpStatus
import test.com.sirloin.util.random.randomEnum
import test.endpoint.v1.auth.AuthenticationTokenResponse
import test.endpoint.v1.userId
import testcase.large.endpoint.v1.LargeTestBaseV1
import java.util.*

fun LargeTestBaseV1.createUserApi(
    request: CreateUserRequest = CreateUserRequest.random()
): Pair<SimpleUserResponse, AuthenticationTokenResponse> {
    val (response, headers) = jsonRequest()
        .body(request)
        .post(ApiPathsV1.USER)
        .expect2xx(SimpleUserResponse::class)

    val (accessToken, refreshToken) = with(headers) {
        get(LoginController.HEADER_AUTHORIZATION)?.value to get(LoginController.HEADER_X_AUTHORIZATION_RESPONSE)?.value
    }

    assertSoftly {
        accessToken shouldNotBe null
        refreshToken shouldNotBe null
    }

    return Pair(response, AuthenticationTokenResponse(accessToken!!, refreshToken!!))
}

fun LargeTestBaseV1.queryUserApi(
    userId: UUID? = null,
    authToken: AuthenticationTokenResponse? = null
): SimpleUserResponse {
    val (response, _) = jsonRequest().apply {
        if (authToken != null) {
            authenticatedBy(authToken.accessToken)
        }
    }
        .get(ApiPathsV1.userId(userId))
        .expect2xx(SimpleUserResponse::class)

    return response
}

fun LargeTestBaseV1.createRandomUser(
    request: CreateUserRequest = CreateUserRequest.random(
        authType = randomEnum(AuthenticationTypeDto::class) { it.domainValue.isThirdPartyAuth }
    )
): Triple<SimpleUserResponse, ProviderAuthenticationResponse, AuthenticationTokenResponse> {
    val spyProviderAuthRepository = getSpyProviderAuthRepository()

    val providerAuthentication = with(spyProviderAuthRepository) {
        val mockProviderAuth = setProviderAuthVerified(request.authType.domainValue, request.providerAuthToken)!!
        if (mockProviderAuth.type == ProviderAuthentication.Type.EMAIL_AND_PASSWORD) {
            setFindByEmailAuthIdentity(mockProviderAuth.providerId, mockProviderAuth.password ?: "") { _, _ -> null }
        } else {
            setFindByProviderAuthIdentity(mockProviderAuth.type, mockProviderAuth.providerId) { _, _ -> null }
        }

        return@with ProviderAuthenticationResponse(
            AuthenticationTypeDto.from(mockProviderAuth.type),
            mockProviderAuth.providerId
        )
    }

    val createdUser = createUserApi(request)

    return Triple(createdUser.first, providerAuthentication, createdUser.second)
}
