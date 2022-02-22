/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.endpoint.v1.user

import com.sirloin.sandbox.server.api.endpoint.v1.ApiPathsV1
import com.sirloin.sandbox.server.api.endpoint.v1.user.request.CreateUserRequest
import com.sirloin.sandbox.server.api.endpoint.v1.user.response.UserResponse
import com.sirloin.sandbox.server.core.domain.user.service.CreateUserService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

/**
 * ```
 * POST /user
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
interface CreateUserController {
    @RequestMapping(
        path = [ApiPathsV1.USER],
        method = [RequestMethod.POST]
    )
    fun create(@Valid @RequestBody req: CreateUserRequest): UserResponse
}

@RestController
class CreateUserControllerImpl(
    private val svc: CreateUserService
) : CreateUserController {
    override fun create(req: CreateUserRequest): UserResponse {
        val createdUser = req.run {
            svc.createUser(
                nickname = req.nickname,
                profileImageUrl = req.profileImageUrl
            )
        }

        return UserResponse.from(createdUser)
    }
}
