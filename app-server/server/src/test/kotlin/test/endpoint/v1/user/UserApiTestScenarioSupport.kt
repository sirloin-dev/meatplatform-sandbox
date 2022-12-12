/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.endpoint.v1.user

import net.meatplatform.sandbox.endpoint.v1.ApiPathsV1
import net.meatplatform.sandbox.endpoint.v1.auth.common.AuthenticationTypeDto
import net.meatplatform.sandbox.endpoint.v1.auth.common.ProviderAuthenticationResponse
import net.meatplatform.sandbox.endpoint.v1.user.common.SimpleUserResponse
import net.meatplatform.sandbox.endpoint.v1.user.create.CreateUserRequest
import testcase.large.endpoint.v1.LargeTestBaseV1

fun LargeTestBaseV1.createUserApi(
    request: CreateUserRequest = CreateUserRequest.random()
): SimpleUserResponse =
    jsonRequest()
        .body(request)
        .post(ApiPathsV1.USER)
        .expect2xx(SimpleUserResponse::class)

fun LargeTestBaseV1.createRandomUser(
    request: CreateUserRequest = CreateUserRequest.random()
): Pair<SimpleUserResponse, ProviderAuthenticationResponse> {
    val spyProviderAuthRepository = getSpyProviderAuthRepository()

    val providerAuthentication = with(spyProviderAuthRepository) {
        val mockProviderAuth = setProviderAuthVerified(request.authType.domainValue, request.providerAuthToken)
        setIdentity(mockProviderAuth.type, mockProviderAuth.providerId) { _, _ -> null }

        return@with ProviderAuthenticationResponse(
            AuthenticationTypeDto.from(mockProviderAuth.type),
            mockProviderAuth.providerId
        )
    }

    val createdUser = createUserApi(request)
    return createdUser to providerAuthentication
}