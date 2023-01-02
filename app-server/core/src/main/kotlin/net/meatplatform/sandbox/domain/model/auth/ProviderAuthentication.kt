/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.model.auth

import com.sirloin.jvmlib.text.isNullOrUnicodeBlank
import net.meatplatform.sandbox.util.EnumDeserializer
import net.meatplatform.sandbox.util.SerializableEnum

/**
 * @since 2022-02-14
 */
interface ProviderAuthentication {
    val type: Type

    /**
     * 해당 인증타입 내의 이용자 식별 정보. 직접인증인 경우 email,
     * 소셜 서비스 인증인 경우 개별 소셜서비스의 id 가 된다.
     */
    val providerId: String

    /**
     * 만약 [type] 이 [Type.EMAIL_AND_PASSWORD] 인 경우, 어떤 값이 채워져 있다. 다른 수단인 경우 `null`.
     */
    val password: String?

    /**
     * 이용자 식별 정보 중, 해당 인증타입에서 제공하는 이용자의 이름. 빈 값일 수 있습니다.
     */
    val name: String

    enum class Type(override val code: String) : SerializableEnum<String> {
        /**
         * IP 주소 인증: Access Token 발급시에만 활용.
         */
        IP_ADDRESS("ip"),

        /**
         * 사용자가 직접 입력한 Email + 비밀번호 인증.
         */
        EMAIL_AND_PASSWORD("e"),

        /**
         * Google 인증.
         *
         * https://developers.google.com/identity/sign-in/web/backend-auth?hl=en#calling-the-tokeninfo-endpoint
         */
        GOOGLE("g"),

        /**
         * Apple 인증. Google/Facebook 인증과 달리, 이용자의 Token 을 직접 validate 해야 한다. (OAuth 2.0)
         *
         * https://developer.apple.com/documentation/sign_in_with_apple/fetch_apple_s_public_key_for_verifying_token_signature
         */
        APPLE("a");

        companion object : EnumDeserializer<String, Type> {
            override fun from(code: String?): Type? = values().firstOrNull { it.code == code }
        }
    }

    companion object {
        fun create(
            type: Type,
            providerId: String,
            password: String? = null,
            name: String = ""
        ): ProviderAuthentication {
            if (type == Type.EMAIL_AND_PASSWORD && password.isNullOrUnicodeBlank()) {
                throw IllegalArgumentException("Password must not be empty with email authentication.")
            }

            return ProviderAuthenticationImpl(
                type = type,
                providerId = providerId,
                password = password,
                name = name
            )
        }
    }
}

internal data class ProviderAuthenticationImpl(
    override val type: ProviderAuthentication.Type,
    override val providerId: String,
    override val password: String?,
    override val name: String
) : ProviderAuthentication {
    companion object {
        fun from(src: ProviderAuthentication): ProviderAuthenticationImpl = with(src) {
            if (this is ProviderAuthenticationImpl) {
                this
            } else {
                ProviderAuthenticationImpl(
                    type = type,
                    providerId = providerId,
                    password = password,
                    name = name
                )
            }
        }
    }
}
