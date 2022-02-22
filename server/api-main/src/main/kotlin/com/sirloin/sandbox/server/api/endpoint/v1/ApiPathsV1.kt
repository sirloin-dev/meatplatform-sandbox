/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.endpoint.v1

object ApiPathsV1 {
    const val PATH_VAR_UUID = "uuid"

    const val V1 = "/v1"

    const val USER = "$V1/user"
    const val USER_UUID = "$V1/user/{$PATH_VAR_UUID}"

    fun userWithUuid(uuid: Any) =
        USER_UUID.replaceFirst("{$PATH_VAR_UUID}", uuid.toString())
}
