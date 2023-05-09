/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1.user.query

import net.meatplatform.sandbox.endpoint.v1.user.QueryUserController
import net.meatplatform.sandbox.endpoint.v1.user.common.SimpleUserResponse
import org.springframework.web.bind.annotation.RestController

/**
 * @since 2023-05-09
 */
@RestController
internal class QueryUserControllerImpl : QueryUserController {
    override fun query(userId: String?): SimpleUserResponse {
        System.err.println("Query user: $userId")

        TODO("Not yet implemented")
    }
}
