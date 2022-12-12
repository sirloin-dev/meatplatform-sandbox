/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.endpoint.v1.user

import com.github.javafaker.Faker
import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication
import net.meatplatform.sandbox.endpoint.v1.auth.common.AuthenticationTypeDto
import net.meatplatform.sandbox.endpoint.v1.auth.common.AuthenticationTypeDto.EMAIL_AND_PASSWORD
import net.meatplatform.sandbox.endpoint.v1.user.create.CreateUserRequest
import test.com.sirloin.util.random.randomEnum
import test.util.randomAlphanumeric

fun CreateUserRequest.Companion.random(
    authType: AuthenticationTypeDto = randomEnum(AuthenticationTypeDto::class),
    email: String? = when (authType) {
        EMAIL_AND_PASSWORD -> Faker().internet().emailAddress()
        else -> null
    },
    password: String? = if (authType == EMAIL_AND_PASSWORD) {
        randomAlphanumeric(16, 16)
    } else {
        null
    },
    providerAuthToken: String? = if (authType != EMAIL_AND_PASSWORD) {
        randomAlphanumeric(128, 128)
    } else {
        null
    },
    nickname: String = Faker().funnyName().name(),
    profileImageUrl: String? = if (Faker().random().nextBoolean()) {
        Faker().internet().image()
    } else {
        null
    }
) = CreateUserRequest(
    authType = authType,
    email = email,
    password = password,
    providerAuthToken = providerAuthToken,
    nickname = nickname,
    profileImageUrl = profileImageUrl
)

fun AuthenticationTypeDto.Companion.from(domainValue: ProviderAuthentication.Type): AuthenticationTypeDto =
    AuthenticationTypeDto.values().first { it.domainValue == domainValue }
