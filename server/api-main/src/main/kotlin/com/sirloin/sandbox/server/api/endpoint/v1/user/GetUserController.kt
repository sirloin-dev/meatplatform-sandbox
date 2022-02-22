package com.sirloin.sandbox.server.api.endpoint.v1.user

import com.sirloin.sandbox.server.api.endpoint.util.uuidStringToUuid
import com.sirloin.sandbox.server.api.endpoint.v1.ApiPathsV1
import com.sirloin.sandbox.server.api.endpoint.v1.user.response.UserResponse
import com.sirloin.sandbox.server.core.domain.user.service.GetUserService
import com.sirloin.sandbox.server.core.i18n.LocaleProvider
import org.slf4j.Logger
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * ```
 * GET /user/{userId}
 *
 * Content-Type: application/json
 * ```
 *
 * @since 2022-02-14
 */
@RequestMapping(
    produces = [MediaType.APPLICATION_JSON_VALUE],
    consumes = [MediaType.APPLICATION_JSON_VALUE]
)
interface GetUserController {
    @RequestMapping(
        path = [ApiPathsV1.USER_UUID],
        method = [RequestMethod.GET]
    )
    fun create(@PathVariable(ApiPathsV1.PATH_VAR_UUID) uuidStr: String): UserResponse
}

@RestController
class GetUserControllerImpl(
    private val svc: GetUserService,
    private val localeProvider: LocaleProvider,
    private val log: Logger,
) : GetUserController {
    override fun create(uuidStr: String): UserResponse {
        val uuid = uuidStringToUuid(uuidStr, localeProvider, log)

        return UserResponse.from(svc.getUserByUuid(uuid))
    }
}
