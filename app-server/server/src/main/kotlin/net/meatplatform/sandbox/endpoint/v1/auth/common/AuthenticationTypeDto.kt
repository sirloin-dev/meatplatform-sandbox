/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1.auth.common

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication
import net.meatplatform.sandbox.util.EnumDeserializer
import net.meatplatform.sandbox.util.SerializableEnum

/**
 * @since 2022-02-14
 */
enum class AuthenticationTypeDto(
    @JsonValue override val code: String,
    val domainValue: ProviderAuthentication.Type
) : SerializableEnum<String> {
    EMAIL_AND_PASSWORD("e", ProviderAuthentication.Type.EMAIL_AND_PASSWORD),
    GOOGLE("g", ProviderAuthentication.Type.GOOGLE),
    APPLE("a", ProviderAuthentication.Type.APPLE);

    companion object : EnumDeserializer<String, AuthenticationTypeDto> {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @JvmStatic
        override fun from(code: String?): AuthenticationTypeDto? =
            values().firstOrNull { it.code == code }
    }
}
