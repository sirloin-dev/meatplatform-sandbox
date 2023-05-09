/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1

/**
 * @since 2022-02-14
 */
object ApiPathsV1 {
    const val V1 = "/v1"

    const val AUTH = "$V1/auth"
    const val AUTH_LOGIN = "$AUTH/login"

    const val USER = "$V1/user"
    const val USER_USER_ID = "$USER/{${ApiVariableV1.USER_ID}}"
}
