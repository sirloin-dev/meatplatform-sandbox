/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox

import net.meatplatform.sandbox.util.EnumDeserializer
import net.meatplatform.sandbox.util.SerializableEnum

/**
 * build.gradle 과 연동한 App Build Profile.
 * [code] 로 정의하는 문자열은 모두 packaging.gradle 파일의 내용과 일치해야 합니다.
 *
 * @since 2022-02-14
 */
enum class AppProfile(override val code: String) : SerializableEnum<String> {
    LOCAL(code = "local"),
    ALPHA(code = "alpha"),
    BETA(code = "beta"),
    RELEASE(code = "release");

    companion object : EnumDeserializer<String, AppProfile> {
        override fun from(code: String?): AppProfile? =
            values().firstOrNull { it.code == code }
    }
}
