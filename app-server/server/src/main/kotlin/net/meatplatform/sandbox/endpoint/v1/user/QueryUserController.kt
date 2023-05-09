/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1.user

import net.meatplatform.sandbox.endpoint.v1.ApiPathsV1
import net.meatplatform.sandbox.endpoint.v1.ApiVariableV1
import net.meatplatform.sandbox.endpoint.v1.user.common.SimpleUserResponse
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

/**
 * ```
 * GET /user, GET /user/{USER_ID}
 *
 * Content-Type: application/json
 * ```
 *
 * 인증한 이용자인 경우 자신 또는 특정 이용자의 정보를 조회합니다.
 *
 * @since 2022-02-14
 */
@RequestMapping(
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
interface QueryUserController {
    @GetMapping(path = [ApiPathsV1.USER, ApiPathsV1.USER_USER_ID])
    fun query(@PathVariable(ApiVariableV1.USER_ID) userId: String?): SimpleUserResponse
}
