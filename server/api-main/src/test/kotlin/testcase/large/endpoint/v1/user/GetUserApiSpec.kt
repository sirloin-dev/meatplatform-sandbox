/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.large.endpoint.v1.user

import com.github.javafaker.Faker
import com.sirloin.sandbox.server.api.endpoint.v1.ApiPathsV1
import com.sirloin.sandbox.server.api.endpoint.v1.user.response.UserResponse
import com.sirloin.sandbox.server.core.exception.MtExceptionCode
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import test.com.sirloin.util.text.randomFillChars
import test.large.endpoint.v1.user.createRandomUser
import test.large.endpoint.v1.user.userInfoResponseFieldsSnippet
import testcase.large.endpoint.v1.UserTestBaseV1
import java.util.*

class GetUserApiSpec : UserTestBaseV1() {
    @DisplayName("생성한 이용자가 서버에 존재하는지 확인한다")
    @Test
    fun getExistingUser() {
        // given:
        val createdUser = createRandomUser()

        // then:
        val response = jsonRequest()
            .withDocumentation(null, userInfoResponseFieldsSnippet())
            .get(ApiPathsV1.userWithUuid(createdUser.uuid))
            .expect2xx(UserResponse::class)

        // expect:
        assertThat(response, `is`(createdUser))
    }

    @DisplayName("파라미터에 UUID 가 아닌 값을 입력하면 오류가 발생한다 (HTTP 400)")
    @Test
    fun errorByNonUuid() {
        // given:
        val notAUuid = Faker().letterify(randomFillChars('?', 1))

        // then:
        jsonRequest()
            .withErrorDocumentation()
            .get(ApiPathsV1.userWithUuid(notAUuid))
            .expect4xx(HttpStatus.BAD_REQUEST)
            .withExceptionCode(MtExceptionCode.WRONG_INPUT)
    }

    @DisplayName("없는 이용자를 get 하려고 하면 USER_NOT_FOUND 에러가 발생한다 (HTTP 404)")
    @Test
    fun errorByNotExistingUser() {
        // given:
        val randomUuid = UUID.randomUUID()

        // then:
        jsonRequest()
            .withErrorDocumentation()
            .get(ApiPathsV1.userWithUuid(randomUuid))
            .expect4xx(HttpStatus.NOT_FOUND)
            .withExceptionCode(MtExceptionCode.USER_NOT_FOUND)
    }
}
