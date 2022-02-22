/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.large.endpoint.v1.user

import com.github.javafaker.Faker
import com.sirloin.sandbox.server.api.endpoint.v1.ApiPathsV1
import com.sirloin.sandbox.server.api.endpoint.v1.user.request.CreateUserRequest
import com.sirloin.sandbox.server.api.endpoint.v1.user.response.UserResponse
import com.sirloin.sandbox.server.core.domain.user.User
import com.sirloin.sandbox.server.core.exception.MtExceptionCode
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.http.HttpStatus
import test.com.sirloin.util.text.randomFillChars
import test.large.endpoint.v1.user.createUserRequestFieldsSnippet
import test.large.endpoint.v1.user.random
import test.large.endpoint.v1.user.userInfoResponseFieldsSnippet
import testcase.large.endpoint.v1.UserTestBaseV1

@Suppress("ClassName")  // Test fixture 이름 잘 짓기 위해 무시
class CreateUserApiSpec : UserTestBaseV1() {
    @DisplayName("입력한 값을 이용해 이용자를 생성한다")
    @Test
    fun userCreated() {
        // given:
        val request = CreateUserRequest.random()

        // then:
        val response = jsonRequest()
            .withDocumentation(createUserRequestFieldsSnippet(), userInfoResponseFieldsSnippet())
            .body(request)
            .post(ApiPathsV1.USER)
            .expect2xx(UserResponse::class)

        // expect:
        assertAll(
            { assertThat(response.nickname, `is`(request.nickname)) },
            { assertThat(response.profileImageUrl, `is`(request.profileImageUrl)) }
        )
    }

    @DisplayName("Nickname 의 길이가 너무 짧거나 긴 경우:")
    @Nested
    inner class errorByInvalidParameters {
        @DisplayName("닉네임이 ${User.NICKNAME_SIZE_MIN} 보다 짧으면 오류가 발생한다 (HTTP 400)")
        @Test
        fun nickNameTooShort() {
            // given:
            val request = CreateUserRequest.random(nickname = "")

            // then:
            jsonRequest()
                .withErrorDocumentation()
                .body(request)
                .post(ApiPathsV1.USER)
                .expect4xx(HttpStatus.BAD_REQUEST)
                .withExceptionCode(MtExceptionCode.WRONG_INPUT)
        }

        @DisplayName("닉네임이 ${User.NICKNAME_SIZE_MAX} 보다 길면 오류가 발생한다 (HTTP 400)")
        @Test
        fun nickNameTooLong() {
            // given:
            val request = CreateUserRequest.random(
                nickname = Faker().letterify(
                    randomFillChars('?', User.NICKNAME_SIZE_MAX + 1, User.NICKNAME_SIZE_MAX + 1)
                )
            )

            // then:
            jsonRequest()
                .withErrorDocumentation()
                .body(request)
                .post(ApiPathsV1.USER)
                .expect4xx(HttpStatus.BAD_REQUEST)
                .withExceptionCode(MtExceptionCode.WRONG_INPUT)
        }
    }
}
