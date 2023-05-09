/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.security.authentication

import net.meatplatform.sandbox.util.EnumDeserializer
import net.meatplatform.sandbox.util.SerializableEnum

/**
 * @since 2022-03-20
 */
enum class HttpAuthenticationScheme(override val code: String) : SerializableEnum<String> {
    /** HTTP 기본 username - password 인증 */
    BASIC("Basic"),

    /** RFC 6750: OAuth 2.0 Bearer Token 인증. Token Bearer(토큰 소유자) 가 토큰 관리의 모든 책임을 져야 한다. */
    BEARER("Bearer"),
    TOKEN("Token"),
    UNDEFINED("");

    companion object : EnumDeserializer<String, HttpAuthenticationScheme> {
        // Servlet 구현체 또는 클라이언트 구현에 따라 입력이 모두 소문자나 대문자로 오는 경우가 있음
        override fun from(code: String?): HttpAuthenticationScheme =
            values().firstOrNull { it.code.equals(code, true) } ?: UNDEFINED
    }
}
