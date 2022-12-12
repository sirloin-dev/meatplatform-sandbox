/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.domain.usecase.auth

import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication
import test.com.sirloin.util.random.randomEnum
import test.util.randomAlphanumeric

fun ProviderAuthentication.Companion.random(
    type: ProviderAuthentication.Type = randomEnum(ProviderAuthentication.Type::class),
    providerId: String = randomAlphanumeric(24, 24),
    password: String? = if (type == ProviderAuthentication.Type.EMAIL_AND_PASSWORD) {
        randomAlphanumeric(16, 16)
    } else {
        null
    },
    name: String = ""
) = create(
    type = type,
    providerId = providerId,
    password = password,
    name = name
)
