/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.endpoint.v1.auth

import net.meatplatform.sandbox.endpoint.v1.auth.common.AuthenticationTypeDto
import net.meatplatform.sandbox.endpoint.v1.auth.login.LoginRequest
import test.SharedTestObjects.faker
import test.com.sirloin.util.random.randomEnum
import test.util.randomAlphanumeric

fun LoginRequest.Companion.random(
    authType: AuthenticationTypeDto = randomEnum(AuthenticationTypeDto::class),
    email: String? = when (authType) {
        AuthenticationTypeDto.EMAIL_AND_PASSWORD -> faker.internet().emailAddress()
        else -> null
    },
    password: String? = if (authType == AuthenticationTypeDto.EMAIL_AND_PASSWORD) {
        randomAlphanumeric(16, 16)
    } else {
        null
    },
    providerAuthToken: String? = if (authType != AuthenticationTypeDto.EMAIL_AND_PASSWORD) {
        randomAlphanumeric(128, 128)
    } else {
        null
    }
) = LoginRequest(
    authType = authType,
    email = email,
    password = password,
    providerAuthToken = providerAuthToken
)
