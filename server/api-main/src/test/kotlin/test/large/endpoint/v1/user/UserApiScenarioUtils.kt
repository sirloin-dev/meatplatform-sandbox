/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package test.large.endpoint.v1.user

import com.sirloin.sandbox.server.api.endpoint.v1.ApiPathsV1
import com.sirloin.sandbox.server.api.endpoint.v1.user.request.CreateUserRequest
import com.sirloin.sandbox.server.api.endpoint.v1.user.response.DeletedUserResponse
import com.sirloin.sandbox.server.api.endpoint.v1.user.response.UserResponse
import testcase.large.endpoint.v1.LargeTestBaseV1
import java.util.*

fun LargeTestBaseV1.createRandomUser(
    nickname: String? = null,
    profileImageUrl: String? = null
): UserResponse =
    jsonRequest()
        .body(
            CreateUserRequest.random(
                nickname = nickname,
                profileImageUrl = profileImageUrl
            )
        )
        .post(ApiPathsV1.USER)
        .expect2xx(UserResponse::class)

fun LargeTestBaseV1.deleteUser(
    uuid: UUID
): DeletedUserResponse =
    jsonRequest()
        .delete(ApiPathsV1.userWithUuid(uuid))
        .expect2xx(DeletedUserResponse::class)
