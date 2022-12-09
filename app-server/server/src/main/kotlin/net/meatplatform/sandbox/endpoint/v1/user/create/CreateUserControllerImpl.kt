/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1.user.create

import net.meatplatform.sandbox.domain.usecase.user.CreateUserUseCase
import net.meatplatform.sandbox.endpoint.v1.user.CreateUserController
import net.meatplatform.sandbox.endpoint.v1.user.common.SimpleUserResponse
import net.meatplatform.sandbox.exception.external.IllegalHttpMessageException
import org.springframework.web.bind.annotation.RestController

/**
 * @since 2022-02-14
 */
@RestController
internal class CreateUserControllerImpl(
    private val useCase: CreateUserUseCase
) : CreateUserController {
    override fun create(req: CreateUserRequest): SimpleUserResponse {
        if (!req.isAuthenticationFilled) {
            throw IllegalHttpMessageException()
        }

        val createdUser = useCase.createUser(req)

        return SimpleUserResponse.from(createdUser)
    }
}
