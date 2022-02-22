/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.endpoint.v1.user.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.sirloin.sandbox.server.api.validation.UnicodeCharsLength
import com.sirloin.sandbox.server.core.domain.user.User

/**
 * @since 2022-02-14
 */
@JsonDeserialize
data class CreateUserRequest(
    @get:UnicodeCharsLength(
        min = User.NICKNAME_SIZE_MIN,
        max = User.NICKNAME_SIZE_MAX,
        message = "`nickname` must between ${User.NICKNAME_SIZE_MIN} and ${User.NICKNAME_SIZE_MAX} characters."
    )
    @JsonProperty
    @JsonPropertyDescription(DESC_NICKNAME)
    val nickname: String,

    @JsonProperty
    @JsonPropertyDescription(DESC_PROFILE_IMAGE_URL)
    val profileImageUrl: String
) {
    companion object {
        const val DESC_NICKNAME = "${User.NICKNAME_SIZE_MIN}자 이상, ${User.NICKNAME_SIZE_MAX}자 이하의 이용자 닉네임."
        const val DESC_PROFILE_IMAGE_URL = "Profile image URL"
    }
}
