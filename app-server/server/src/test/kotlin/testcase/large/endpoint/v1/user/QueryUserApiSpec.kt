/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.large.endpoint.v1.user

import io.kotest.matchers.shouldBe
import net.meatplatform.sandbox.endpoint.v1.ApiPathsV1
import net.meatplatform.sandbox.endpoint.v1.user.common.SimpleUserResponse
import net.meatplatform.sandbox.exception.ErrorCodeBook
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import test.endpoint.v1.auth.AuthenticationTokenResponse
import test.endpoint.v1.user.createRandomUser
import test.endpoint.v1.user.queryUserApi
import testcase.large.endpoint.v1.LargeTestBaseV1

/**
 * @since 2023-05-09
 */
class QueryUserApiSpec : LargeTestBaseV1() {
    @DisplayName("인증하지 않은 사용자는 이용자 정보를 조회할 수 없다.")
    @Test
    fun errorIfUserIsNotAuthenticated() {
        jsonRequest()
            .get(ApiPathsV1.USER)
            .expect4xx(HttpStatus.UNAUTHORIZED)
            .withExceptionCode(ErrorCodeBook.UNAUTHENTICATED)
    }

    @DisplayName("인증한 이용자는:")
    @Nested
    inner class WhenUserIsAuthenticated {
        private lateinit var createdUser: SimpleUserResponse
        private lateinit var authToken: AuthenticationTokenResponse

        @BeforeEach
        fun setup() {
            val (user, _, token) = createRandomUser()
            createdUser = user
            authToken = token
        }

        @DisplayName("자기 자신의 정보를 조회할 수 있다.")
        @Test
        fun canQueryThemselves() {
//            // then:
//            val result = queryUserApi(authToken = authToken)
//
//            // expect:
//            result shouldBe createdUser

            jsonRequest()
                .authenticatedBy(authToken.accessToken)
                .get(ApiPathsV1.USER)
                .expect5xx(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
