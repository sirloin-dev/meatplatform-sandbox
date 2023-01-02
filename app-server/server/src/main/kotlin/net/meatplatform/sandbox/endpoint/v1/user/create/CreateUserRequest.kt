/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1.user.create

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.sirloin.jvmlib.text.isNullOrUnicodeBlank
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.model.user.SimpleUser
import net.meatplatform.sandbox.domain.usecase.user.CreateUserUseCase
import net.meatplatform.sandbox.endpoint.v1.auth.common.AuthenticationTypeDto
import net.meatplatform.sandbox.util.validation.UnicodeCharsLength

/**
 * @since 2022-02-14
 */
@JsonDeserialize
data class CreateUserRequest(
    @get:NotNull
    @JsonProperty
    @JsonPropertyDescription(DESC_AUTH_TYPE)
    val authType: AuthenticationTypeDto,

    @get:Email
    @JsonProperty
    @JsonPropertyDescription(DESC_EMAIL)
    override val email: String?,

    @JsonProperty
    @JsonPropertyDescription(DESC_PASSWORD)
    override val password: String?,

    @JsonProperty
    @JsonPropertyDescription(DESC_PROVIDER_AUTH_TOKEN)
    override val providerAuthToken: String?,

    @get:UnicodeCharsLength(
        min = NICKNAME_MIN,
        max = NICKNAME_MAX,
        message = "`nickname` must between $NICKNAME_MIN and $NICKNAME_MAX characters."
    )
    @JsonProperty
    @JsonPropertyDescription(DESC_NICKNAME)
    override val nickname: String,

    @JsonProperty
    @JsonPropertyDescription(DESC_PROFILE_IMAGE_URL)
    override val profileImageUrl: String?
) : CreateUserUseCase.Message {
    @get:JsonIgnore
    val isAuthenticationFilled: Boolean
        get() = when (authType) {
            AuthenticationTypeDto.EMAIL_AND_PASSWORD -> !email.isNullOrUnicodeBlank()
            AuthenticationTypeDto.GOOGLE,
            AuthenticationTypeDto.APPLE -> !providerAuthToken.isNullOrUnicodeBlank()
        }

    @get:JsonIgnore
    override val authenticationType: ProviderAuthentication.Type
        get() = authType.domainValue

    companion object {
        private const val NICKNAME_MIN = SimpleUser.NICKNAME_SIZE_MIN
        private const val NICKNAME_MAX = SimpleUser.NICKNAME_SIZE_MAX

        const val DESC_AUTH_TYPE = "이용자 생성 및 확인에 활용할 인증 정보."
        const val DESC_EMAIL = "authType 이 Email 인 경우, null 이 아니어야 합니다."
        const val DESC_PASSWORD = "authType 이 Email 인 경우, null 이 아니어야 합니다. 클라이언트는 이용자의 평문 암호를" +
                "적절히 hash 한 값을 전달해 주세요. "
        const val DESC_PROVIDER_AUTH_TOKEN = "authType 이 Google 또는 Apple 인 경우 null 이 아니어야 합니다."
        const val DESC_NICKNAME = "$NICKNAME_MIN 자 이상, $NICKNAME_MAX 자 이하의 이용자 닉네임."
        const val DESC_PROFILE_IMAGE_URL = "Profile image URL"
    }
}
