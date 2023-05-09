/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1.user.query

import net.meatplatform.sandbox.endpoint.v1.user.QueryUserController
import net.meatplatform.sandbox.endpoint.v1.user.common.SimpleUserResponse
import net.meatplatform.sandbox.security.authentication.VerifiedAuthentication
import net.meatplatform.sandbox.util.uuidStringToUuid
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RestController

/**
 * @since 2023-05-09
 */
@RestController
internal class QueryUserControllerImpl : QueryUserController {
    override fun query(userId: String?, authentication: VerifiedAuthentication): SimpleUserResponse {
        val trueUserId = if (userId == null) {
            authentication.userId
        } else {
            uuidStringToUuid(userId)
        }

        TODO("Not yet implemented. Query info of $trueUserId here.")
    }
}
