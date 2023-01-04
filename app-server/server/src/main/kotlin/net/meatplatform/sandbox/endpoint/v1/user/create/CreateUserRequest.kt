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
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.user.SimpleUser
import net.meatplatform.sandbox.domain.user.usecase.CreateUserUseCase
import net.meatplatform.sandbox.endpoint.v1.auth.common.AuthenticationTypeDto
import net.meatplatform.sandbox.endpoint.v1.auth.login.LoginRequest
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

        const val DESC_AUTH_TYPE = LoginRequest.DESC_AUTH_TYPE
        const val DESC_EMAIL = LoginRequest.DESC_EMAIL
        const val DESC_PASSWORD = LoginRequest.DESC_PASSWORD
        const val DESC_PROVIDER_AUTH_TOKEN = LoginRequest.DESC_PROVIDER_AUTH_TOKEN
        const val DESC_NICKNAME = "$NICKNAME_MIN 자 이상, $NICKNAME_MAX 자 이하의 이용자 닉네임."
        const val DESC_PROFILE_IMAGE_URL = "Profile image URL"
    }
}
