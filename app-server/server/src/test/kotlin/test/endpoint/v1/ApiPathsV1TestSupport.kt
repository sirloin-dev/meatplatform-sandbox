/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.endpoint.v1

import net.meatplatform.sandbox.endpoint.v1.ApiPathsV1
import net.meatplatform.sandbox.endpoint.v1.ApiVariableV1
import java.util.*

fun ApiPathsV1.userId(
    userId: UUID? = null
): String = if (userId == null) {
    USER
} else {
    USER_USER_ID.replace("{${ApiVariableV1.USER_ID}}", userId.toString())
}
