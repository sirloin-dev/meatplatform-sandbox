package com.sirloin.sandbox.server.api.endpoint.v1.user

import com.sirloin.sandbox.server.api.endpoint.util.uuidStringToUuid
import com.sirloin.sandbox.server.api.endpoint.v1.ApiPathsV1
import com.sirloin.sandbox.server.api.endpoint.v1.user.response.DeletedUserResponse
import com.sirloin.sandbox.server.core.domain.user.service.DeleteUserService
import com.sirloin.sandbox.server.core.i18n.LocaleProvider
import org.slf4j.Logger
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * ```
 * DELETE /user/{userId}
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
interface DeleteUserController {
    @RequestMapping(
        path = [ApiPathsV1.USER_UUID],
        method = [RequestMethod.DELETE]
    )
    fun delete(@PathVariable(ApiPathsV1.PATH_VAR_UUID) uuidStr: String): DeletedUserResponse
}

@RestController
class DeleteUserControllerImpl(
    private val svc: DeleteUserService,
    private val localeProvider: LocaleProvider,
    private val log: Logger,
) : DeleteUserController {
    override fun delete(uuidStr: String): DeletedUserResponse {
        val uuid = uuidStringToUuid(uuidStr, localeProvider, log)

        val deletedUser = svc.deleteUserByUuid(uuid)

        return DeletedUserResponse(deletedUser.uuid)
    }
}
