/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1.auth.login

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.sirloin.jvmlib.text.isNullOrUnicodeBlank
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import net.meatplatform.sandbox.domain.auth.usecase.LoginUseCase
import net.meatplatform.sandbox.endpoint.v1.auth.common.AuthenticationTypeDto
import net.meatplatform.sandbox.exception.external.IllegalHttpMessageException

/**
 * @since 2022-12-09
 */
@JsonDeserialize
data class LoginRequest(
    @get:NotNull
    @JsonProperty
    @JsonPropertyDescription(DESC_AUTH_TYPE)
    val authType: AuthenticationTypeDto,

    @get:Email
    @JsonProperty
    @JsonPropertyDescription(DESC_EMAIL)
    val email: String?,

    @JsonProperty
    @JsonPropertyDescription(DESC_PASSWORD)
    val password: String?,

    @JsonProperty
    @JsonPropertyDescription(DESC_PROVIDER_AUTH_TOKEN)
    val providerAuthToken: String?
) {
    fun toLoginMessage(): LoginUseCase.Message = when (authType) {
        AuthenticationTypeDto.EMAIL_AND_PASSWORD -> {
            if (email.isNullOrUnicodeBlank() || password.isNullOrUnicodeBlank()) {
                throw IllegalHttpMessageException()
            } else {
                LoginUseCase.EmailLoginMessage(email = requireNotNull(email), password = requireNotNull(password))
            }
        }

        AuthenticationTypeDto.GOOGLE, AuthenticationTypeDto.APPLE -> {
            if (providerAuthToken.isNullOrUnicodeBlank()) {
                throw IllegalHttpMessageException()
            } else {
                LoginUseCase.ThirdPartyAuthLoginMessage(
                    type = authType.domainValue,
                    authToken = requireNotNull(providerAuthToken)
                )
            }
        }
    }

    companion object {
        const val DESC_AUTH_TYPE = "이용자 생성 및 확인에 활용할 인증 정보."
        const val DESC_EMAIL = "authType 이 Email 인 경우, null 이 아니어야 합니다."
        const val DESC_PASSWORD = "authType 이 Email 인 경우, null 이 아니어야 합니다. 클라이언트는 이용자의 평문 암호를" +
                "적절히 hash 한 값을 전달해 주세요."
        const val DESC_PROVIDER_AUTH_TOKEN = "authType 이 Google 또는 Apple 인 경우 null 이 아니어야 합니다."
    }
}
