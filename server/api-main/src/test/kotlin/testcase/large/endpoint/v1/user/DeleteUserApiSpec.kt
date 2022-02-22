/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.large.endpoint.v1.user

import com.sirloin.sandbox.server.api.endpoint.v1.ApiPathsV1
import com.sirloin.sandbox.server.api.endpoint.v1.user.response.DeletedUserResponse
import com.sirloin.sandbox.server.core.exception.MtExceptionCode
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import test.large.endpoint.v1.user.createRandomUser
import test.large.endpoint.v1.user.deleteUser
import test.large.endpoint.v1.user.deletedUserInfoResponseFieldsSnippet
import testcase.large.endpoint.v1.UserTestBaseV1
import java.util.*

class DeleteUserApiSpec : UserTestBaseV1() {
    @DisplayName("특정 이용자를 삭제할 수 있다")
    @Test
    fun deleteExistingUser() {
        // given:
        val createdUser = createRandomUser()

        // then:
        val response = jsonRequest()
            .withDocumentation(emptyList(), deletedUserInfoResponseFieldsSnippet())
            .delete(ApiPathsV1.userWithUuid(createdUser.uuid))
            .expect2xx(DeletedUserResponse::class)

        // expect:
        assertThat(response.uuid, `is`(createdUser.uuid))
    }

    @DisplayName("없는 이용자를 삭제할 수 없다 (HTTP 404)")
    @Test
    fun errorByNotExistingUser() {
        // expect:
        jsonRequest()
            .withErrorDocumentation()
            .delete(ApiPathsV1.userWithUuid(UUID.randomUUID()))
            .expect4xx(HttpStatus.NOT_FOUND)
            .withExceptionCode(MtExceptionCode.USER_NOT_FOUND)
    }

    @DisplayName("삭제한 이용자의 정보를 획득할 수 없다 (HTTP 404)")
    @Test
    fun cannotFindDeletedUser() {
        // given:
        val createdUser = createRandomUser()

        // when:
        val deletedUser = deleteUser(createdUser.uuid)

        // then:
        assertThat(createdUser.uuid, `is`(deletedUser.uuid))

        // expect:
        jsonRequest()
            .withErrorDocumentation()
            .get(ApiPathsV1.userWithUuid(deletedUser.uuid))
            .expect4xx(HttpStatus.NOT_FOUND)
            .withExceptionCode(MtExceptionCode.USER_NOT_FOUND)
    }
}
