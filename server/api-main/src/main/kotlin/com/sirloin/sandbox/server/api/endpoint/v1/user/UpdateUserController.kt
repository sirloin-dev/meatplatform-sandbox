package com.sirloin.sandbox.server.api.endpoint.v1.user

import com.sirloin.sandbox.server.api.endpoint.util.uuidStringToUuid
import com.sirloin.sandbox.server.api.endpoint.v1.ApiPathsV1
import com.sirloin.sandbox.server.api.endpoint.v1.user.request.UpdateUserRequest
import com.sirloin.sandbox.server.api.endpoint.v1.user.response.UserResponse
import com.sirloin.sandbox.server.core.domain.user.service.UpdateUserService
import com.sirloin.sandbox.server.core.i18n.LocaleProvider
import org.slf4j.Logger
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

/**
 * ```
 * PATCH /user/{userId}
 *
 * Content-Type: application/json
 * ```
 *
 * @since 2022-02-14
 */
// POINT: 이 API 에는 심각한 문제가 있습니다. 어떤 문제일까요? 어떻게 개선할 수 있을까요?
@RequestMapping(
    produces = [MediaType.APPLICATION_JSON_VALUE],
    consumes = [MediaType.APPLICATION_JSON_VALUE]
)
interface UpdateUserController {
    @RequestMapping(
        path = [ApiPathsV1.USER_UUID],
        method = [RequestMethod.PATCH]
    )
    fun update(
        @PathVariable(ApiPathsV1.PATH_VAR_UUID) uuidStr: String,
        @Valid @RequestBody req: UpdateUserRequest
    ): UserResponse
}

@RestController
class UpdateUserControllerImpl(
    private val svc: UpdateUserService,
    private val localeProvider: LocaleProvider,
    private val log: Logger,
) : UpdateUserController {
    override fun update(uuidStr: String, req: UpdateUserRequest): UserResponse {
        val uuid = uuidStringToUuid(uuidStr, localeProvider, log)

        return UserResponse.from(
            svc.updateUser(
                uuid = uuid,
                nickname = req.nickname,
                profileImageUrl = req.profileImageUrl
            )
        )
    }
}
