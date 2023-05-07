/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.large.endpoint.v1.auth

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.endpoint.common.response.SimpleValueResponse
import net.meatplatform.sandbox.endpoint.v1.ApiPathsV1
import net.meatplatform.sandbox.endpoint.v1.auth.LoginController.Companion.HEADER_AUTHORIZATION
import net.meatplatform.sandbox.endpoint.v1.auth.LoginController.Companion.HEADER_X_AUTHORIZATION_RESPONSE
import net.meatplatform.sandbox.endpoint.v1.auth.common.AuthenticationTypeDto
import net.meatplatform.sandbox.endpoint.v1.auth.login.LoginRequest
import net.meatplatform.sandbox.endpoint.v1.user.create.CreateUserRequest
import net.meatplatform.sandbox.exception.ErrorCodeBook
import net.meatplatform.sandbox.exception.external.auth.ProviderAuthenticationFailedException
import org.junit.jupiter.api.*
import org.springframework.http.HttpStatus
import test.com.sirloin.util.random.randomEnum
import test.domain.repository.auth.SpyProviderAuthRepository
import test.endpoint.v1.auth.random
import test.endpoint.v1.user.createRandomUser
import test.endpoint.v1.user.from
import test.endpoint.v1.user.random
import testcase.large.endpoint.v1.LargeTestBaseV1
import java.io.IOException

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

    @DisplayName("가입한 적 없는 이용자의 Email 인증정보로 Login 을 시도한 경우 USER_BY_PROVIDER_AUTH_NOT_FOUND 오류가 발생한다 (HTTP 401)")
    @Test
    fun cannotLoginIfUserIsNotRegistered() {
        // given:
        val request = LoginRequest.random(authType = AuthenticationTypeDto.EMAIL_AND_PASSWORD)

        // then:
        jsonRequest()
            .body(request)
            .post(ApiPathsV1.AUTH_LOGIN)
            .expect4xx(HttpStatus.UNAUTHORIZED)
            .withExceptionCode(ErrorCodeBook.USER_BY_PROVIDER_AUTH_NOT_FOUND)
    }

    @DisplayName("외부 로그인 시스템이:")
    @Nested
    inner class WhenThirdPartySystemIs {
        private lateinit var request: LoginRequest

        @BeforeEach
        fun setup() {
            request = LoginRequest.random(authType = randomEnum(AuthenticationTypeDto::class) {
                it.domainValue.isThirdPartyAuth
            })
        }

        @DisplayName("응답이 없다면 EXTERNAL_SYSTEM_NOT_RESPONDING 오류가 발생한다 (HTTP 502)")
        @Test
        fun errorIfNotResponding() {
            // when:
            setThrowException(IOException("제 3자 로그인 시스템 접근 실패"))

            // then:
            jsonRequest()
                .body(request)
                .post(ApiPathsV1.AUTH_LOGIN)
                .expect5xx(HttpStatus.BAD_GATEWAY)
                .withExceptionCode(ErrorCodeBook.EXTERNAL_SYSTEM_NOT_RESPONDING)
        }

        @DisplayName("인증을 거부하면 EXTERNAL_PROVIDER_AUTH_VERIFICATION_FAILED 오류가 발생한다 (HTTP 400)")
        @Test
        fun errorIfRejectingAuthentication() {
            // when:
            setThrowException(
                ProviderAuthenticationFailedException(request.authType.domainValue, request.providerAuthToken!!)
            )

            // then:
            jsonRequest()
                .body(request)
                .post(ApiPathsV1.AUTH_LOGIN)
                .expect4xx(HttpStatus.UNAUTHORIZED)
                .withExceptionCode(ErrorCodeBook.EXTERNAL_PROVIDER_AUTH_VERIFICATION_FAILED)
        }

        private fun setThrowException(exception: Exception) {
            with(spyProviderAuthRepository) {
                setProviderAuthVerified(request.authType.domainValue, request.providerAuthToken) { _, _ ->
                    throw exception
                }
            }
        }
    }

    @DisplayName("가입한적 있는 이용자는 Login 과정을 거쳐 인증토큰 쌍을 획득할 수 있다")
    @Test
    fun userCanObtainAccessTokenPairs() {
        // given:
        val authType = randomEnum(AuthenticationTypeDto::class) { it.domainValue.isThirdPartyAuth }
        val createUserRequest = CreateUserRequest.random(authType = authType)
        val (_, providerAuth) = createRandomUser(createUserRequest)

        // and:
        val request = LoginRequest.random(
            authType = providerAuth.type,
            providerAuthToken = createUserRequest.providerAuthToken
        )

        // when:
        with(spyProviderAuthRepository) {
            setProviderAuthVerified(request.authType.domainValue, request.providerAuthToken) { _, _ ->
                ProviderAuthentication.create(
                    type = providerAuth.type.domainValue,
                    providerId = providerAuth.providerId
                )
            }
        }

        // then:
        val (response, headers) = jsonRequest()
            .body(request)
            .post(ApiPathsV1.AUTH_LOGIN)
            .expect2xx(SimpleValueResponse::class)

        // expect:
        response.value shouldBe true

        // then:
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
