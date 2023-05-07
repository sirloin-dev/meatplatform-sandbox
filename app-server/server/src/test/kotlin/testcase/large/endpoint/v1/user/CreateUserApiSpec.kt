/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.large.endpoint.v1.user

import io.kotest.matchers.string.shouldNotBeBlank
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.endpoint.v1.ApiPathsV1
import net.meatplatform.sandbox.endpoint.v1.auth.LoginController.Companion.HEADER_AUTHORIZATION
import net.meatplatform.sandbox.endpoint.v1.auth.LoginController.Companion.HEADER_X_AUTHORIZATION_RESPONSE
import net.meatplatform.sandbox.endpoint.v1.auth.common.AuthenticationTypeDto
import net.meatplatform.sandbox.endpoint.v1.user.common.SimpleUserResponse
import net.meatplatform.sandbox.endpoint.v1.user.create.CreateUserRequest
import net.meatplatform.sandbox.exception.ErrorCodeBook
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import test.com.sirloin.util.random.randomEnum
import test.domain.repository.auth.SpyProviderAuthRepository
import test.endpoint.v1.user.assertSimpleUserResponse
import test.endpoint.v1.user.createRandomUser
import test.endpoint.v1.user.createUserApi
import test.endpoint.v1.user.random
import testcase.large.endpoint.v1.LargeTestBaseV1

/**
 * @since 2022-02-14
 */
class CreateUserApiSpec : LargeTestBaseV1() {
    private lateinit var spyProviderAuthRepository: SpyProviderAuthRepository

    @BeforeEach
    fun setup() {
        spyProviderAuthRepository = getSpyProviderAuthRepository()
    }

    @AfterEach
    fun teardown() {
        spyProviderAuthRepository.clearMocks()
    }

    @DisplayName("요청에 해당하는 이용자를 생성한다")
    @Test
    fun userIsCreated() {
        // given:
        val authType = AuthenticationTypeDto.GOOGLE
        val request = CreateUserRequest.random(authType = authType)

        // when:
        with(spyProviderAuthRepository) {
            val mockProviderAuth = setProviderAuthVerified(authType.domainValue, request.providerAuthToken)!!
            setFindByProviderAuthIdentity(authType.domainValue, mockProviderAuth.providerId) { _, _ -> null }
        }

        // then:
        val result = createUserApi(request)

        // expect:
        assertSimpleUserResponse(result, isReflecting = request)
    }

    @DisplayName("이미 가입한 이용자의 Social Login 이 일치하는 이용자를 또 생성할 수 없다.")
    @Test
    fun userWithSameSocialIdentityNotCreated() {
        // given:
        val authType = randomEnum(AuthenticationTypeDto::class) { it.domainValue.isThirdPartyAuth }
        val request = CreateUserRequest.random(authType = authType)

        // when:
        val (_, providerAuth) = createRandomUser(request)

        // then:
        with(spyProviderAuthRepository) {
            val expectedProviderAuth = ProviderAuthentication.create(authType.domainValue, providerAuth.providerId)
            setProviderAuthVerified(authType.domainValue, request.providerAuthToken) { _, _ ->
                expectedProviderAuth
            }
            setFindByProviderAuthIdentity(authType.domainValue, providerAuth.providerId) { _, _ ->
                expectedProviderAuth
            }
        }

        // then:
        jsonRequest()
            .body(request)
            .post(ApiPathsV1.USER)
            .expect4xx(HttpStatus.CONFLICT)
            .withExceptionCode(ErrorCodeBook.USER_ALREADY_REGISTERED)
    }

    @DisplayName("이용자를 생성하면 응답 헤더의 Authorization 에 Access Token 과 Refresh Token 이 내려온다.")
    @Test
    fun authorizationHeaderRetrieved() {
        // given:
        val authType = AuthenticationTypeDto.GOOGLE
        val request = CreateUserRequest.random(authType = authType)

        // when:
        with(spyProviderAuthRepository) {
            val mockProviderAuth = setProviderAuthVerified(authType.domainValue, request.providerAuthToken)!!
            setFindByProviderAuthIdentity(authType.domainValue, mockProviderAuth.providerId) { _, _ -> null }
        }

        // then:
        val (_, headers) = jsonRequest()
            .body(request)
            .post(ApiPathsV1.USER)
            .expect2xx(SimpleUserResponse::class)

        // and:
        val (accessToken, refreshToken) = with(headers) {
            get(HEADER_AUTHORIZATION)?.value to get(HEADER_X_AUTHORIZATION_RESPONSE)?.value
        }

        // expect:
        assertAll(
            { accessToken.shouldNotBeBlank() },
            { refreshToken.shouldNotBeBlank() }
        )
    }
}
