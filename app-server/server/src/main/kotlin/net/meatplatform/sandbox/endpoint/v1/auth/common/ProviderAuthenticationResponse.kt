/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1.auth.common

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import net.meatplatform.sandbox.endpoint.v1.ResponseV1

/**
 * @since 2022-02-14
 */
@ResponseV1
@JsonSerialize
data class ProviderAuthenticationResponse(
    @JsonProperty
    @JsonPropertyDescription(DESC_TYPE)
    val type: AuthenticationTypeDto,

    @JsonProperty
    @JsonPropertyDescription(DESC_PROVIDER_ID)
    val providerId: String
) {
    companion object {
        const val DESC_TYPE = "제3자 인증 유형."
        const val DESC_PROVIDER_ID = "제3자 인증 서비스 내의 이용자 id."
    }
}
