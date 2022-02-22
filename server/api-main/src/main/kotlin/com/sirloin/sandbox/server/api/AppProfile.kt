/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api

/**
 * build.gradle 과 연동한 App Build Profile.
 * [profileName] 으로 정의하는 문자열은 모두 packaging.gradle 파일의 내용과 일치해야 합니다.
 *
 * @since 2022-02-14
 */
enum class AppProfile(val profileName: String) {
    LOCAL(profileName = "local"),
    ALPHA(profileName = "alpha"),
    BETA(profileName = "beta"),
    RELEASE(profileName = "release");

    companion object {
        fun from(
            profileName: String?,
            defaultValue: AppProfile = LOCAL
        ) = values().firstOrNull { it.profileName == profileName } ?: defaultValue
    }
}
