/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.endpoint.v1.user.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.sirloin.sandbox.server.api.endpoint.v1.ResponseV1
import com.sirloin.sandbox.server.core.domain.user.User
import java.util.*

/**
 * @since 2022-02-14
 */
@ResponseV1
@JsonSerialize
data class UserResponse(
    @JsonProperty
    @JsonPropertyDescription(DESC_UUID)
    val uuid: UUID,

    @JsonProperty
    @JsonPropertyDescription(DESC_NICKNAME)
    val nickname: String,

    @JsonProperty
    @JsonPropertyDescription(DESC_PROFILE_IMAGE_URL)
    val profileImageUrl: String,
) {
    companion object {
        const val DESC_UUID = "이용자의 고유 id."
        const val DESC_NICKNAME = "이용자가 가입 요청 단계에 입력한 닉네임."
        const val DESC_PROFILE_IMAGE_URL = "이용자가 가입 요청에 입력한 프로파일 URL."

        fun from(src: User): UserResponse = UserResponse(
            uuid = src.uuid,
            nickname = src.nickname,
            profileImageUrl = src.profileImageUrl
        )
    }
}
