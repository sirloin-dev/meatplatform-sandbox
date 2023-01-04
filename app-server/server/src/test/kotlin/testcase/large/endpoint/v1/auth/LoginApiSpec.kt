/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.large.endpoint.v1.auth

import net.meatplatform.sandbox.endpoint.v1.ApiPathsV1
import net.meatplatform.sandbox.endpoint.v1.auth.login.LoginRequest
import net.meatplatform.sandbox.exception.ErrorCodeBook
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import test.domain.repository.auth.SpyProviderAuthRepository
import test.endpoint.v1.auth.random
import testcase.large.endpoint.v1.LargeTestBaseV1

/**
 * @since 2022-12-09
 */
class LoginApiSpec : LargeTestBaseV1() {
    private lateinit var spyProviderAuthRepository: SpyProviderAuthRepository

    @BeforeEach
    fun setup() {
        spyProviderAuthRepository = getSpyProviderAuthRepository()
    }

    @AfterEach
    fun teardown() {
        spyProviderAuthRepository.clearMocks()
    }

    @DisplayName("가입한 적 없는 이용자의 정보로 Login 을 시도한 경우 USER_BY_PROVIDER_AUTH_NOT_FOUND 오류가 발생한다 (HTTP 401)")
    @Test
    fun cannotLoginIfUserIsNotRegistered() {
        // given:
        val request = LoginRequest.random()

        // then:
        jsonRequest()
            .body(request)
            .post(ApiPathsV1.AUTH_LOGIN)
            .expect4xx(HttpStatus.UNAUTHORIZED)
            .withExceptionCode(ErrorCodeBook.USER_BY_PROVIDER_AUTH_NOT_FOUND)
    }

    @DisplayName("외부로그인 인증 실패시 EXTERNAL_AUTH_SERVICE_UNAVAILABLE 오류가 발생한다 (HTTP 401)")
    @Test
    fun cannotLoginIfExternalLoginServiceFails() {
        TODO("Implement this")
    }

    @DisplayName("가입한적 있는 이용자는 Login 과정을 거쳐 인증토큰 쌍을 획득할 수 있다")
    @Test
    fun userCanObtainAccessTokenPairs() {
        TODO("Implement this")
        /*
        // then:
        val result = loginApi(request)

        // expect:
        assertSimpleUserResponse(result, isReflecting = request)
         */
    }

    /*
    @DisplayName("이용자를 생성하면 응답 헤더의 Authorization 에 Access Token 과 Refresh Token 이 내려온다.")
    @Test
    fun authorizationHeaderRetrieved() {
        // given:
        val request = CreateUserRequest.random(authType = AuthenticationTypeDto.GOOGLE)

        // when:
        with(spyProviderAuthRepository) {
            val mockProviderAuth = setProviderAuthVerified(request.authType.domainValue, request.providerAuthToken)
            setIdentity(mockProviderAuth.type, mockProviderAuth.providerId) { _, _ -> null }
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
     */
}
