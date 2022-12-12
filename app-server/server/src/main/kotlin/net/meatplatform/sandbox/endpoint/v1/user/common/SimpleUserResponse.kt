/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1.user.common

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import net.meatplatform.sandbox.domain.model.user.SimpleUser
import net.meatplatform.sandbox.endpoint.v1.ResponseV1
import java.time.Instant
import java.util.*

/**
 * @since 2022-02-14
 */
@ResponseV1
@JsonSerialize
data class SimpleUserResponse(
    @JsonProperty
    @JsonPropertyDescription(DESC_UUID)
    val id: UUID,

    @JsonProperty
    @JsonPropertyDescription(DESC_NICKNAME)
    val nickname: String,

    @JsonProperty
    @JsonPropertyDescription(DESC_PROFILE_IMAGE_URL)
    val profileImageUrl: String?,

    @JsonProperty
    @JsonPropertyDescription(DESC_CREATED_AT)
    val createdAt: Instant,

    @JsonProperty
    @JsonPropertyDescription(DESC_UPDATED_AT)
    val updatedAt: Instant
) {
    companion object {
        const val DESC_UUID = "이용자의 고유 id."
        const val DESC_NICKNAME = "이용자가 가입 요청 단계에 입력한 닉네임."
        const val DESC_PROFILE_IMAGE_URL = "이용자가 가입 요청에 입력한 프로파일 URL."
        const val DESC_CREATED_AT = "이용자가 최초 가입한 시간."
        const val DESC_UPDATED_AT = "이용자가 마지막으로 활동한 시간."

        fun from(src: SimpleUser): SimpleUserResponse = with(src) {
            SimpleUserResponse(
                id = id,
                nickname = nickname,
                profileImageUrl = profileImageUrl,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
