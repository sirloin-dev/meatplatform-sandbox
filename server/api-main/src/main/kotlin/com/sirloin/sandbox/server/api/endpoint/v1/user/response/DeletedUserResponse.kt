/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.endpoint.v1.user.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.sirloin.sandbox.server.api.endpoint.v1.ResponseV1
import java.util.*

/**
 * @since 2022-02-14
 */
@ResponseV1
@JsonSerialize
data class DeletedUserResponse(
    @JsonProperty
    @JsonPropertyDescription(DESC_UUID)
    val uuid: UUID
) {
    companion object {
        const val DESC_UUID = "삭제된 이용자의 고유 id."
    }
}
