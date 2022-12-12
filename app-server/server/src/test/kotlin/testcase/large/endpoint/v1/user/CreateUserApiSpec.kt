/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.large.endpoint.v1.user

import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication
import net.meatplatform.sandbox.endpoint.v1.ApiPathsV1
import net.meatplatform.sandbox.endpoint.v1.auth.common.AuthenticationTypeDto
import net.meatplatform.sandbox.endpoint.v1.user.create.CreateUserRequest
import net.meatplatform.sandbox.exception.ErrorCodeBook
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
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
        val request = CreateUserRequest.random(authType = AuthenticationTypeDto.GOOGLE)

        // when:
        with(spyProviderAuthRepository) {
            val mockProviderAuth = setProviderAuthVerified(request.authType.domainValue, request.providerAuthToken)
            setIdentity(mockProviderAuth.type, mockProviderAuth.providerId) { _, _ -> null }
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
        val request = CreateUserRequest.random(authType = AuthenticationTypeDto.GOOGLE)

        // when:
        val (_, _) = createRandomUser(request)

        // then:
        with(spyProviderAuthRepository) {
            val mockProviderAuth = setProviderAuthVerified(request.authType.domainValue, request.providerAuthToken)
            setIdentity(mockProviderAuth.type, mockProviderAuth.providerId) { type, providerId ->
                ProviderAuthentication.create(type, providerId)
            }
        }

        // then:
        jsonRequest()
            .body(request)
            .post(ApiPathsV1.USER)
            .expect4xx(HttpStatus.CONFLICT)
            .withExceptionCode(ErrorCodeBook.USER_ALREADY_REGISTERED)
    }
}
