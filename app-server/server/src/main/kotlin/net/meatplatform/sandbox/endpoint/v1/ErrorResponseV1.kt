/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription

/**
 * @since 2022-02-14
 */
@ResponseV1
data class ErrorResponseV1(override val body: Body) : ResponseEnvelopeV1<ErrorResponseV1.Body>(Type.ERROR) {
    data class Body(
        @JsonPropertyDescription(DESC_BODY_MESSAGE)
        @JsonProperty
        val message: String,

        @JsonPropertyDescription(DESC_BODY_CODE)
        @JsonProperty
        val code: String,

        @JsonPropertyDescription(DESC_BODY_DETAILS)
        @JsonProperty
        val details: Any? = null
    )

    companion object {
        private const val CODE_LENGTH = "8"
        const val DESC_BODY_MESSAGE = "클라이언트의 accept-language 헤더에 맞는 언어로 출력한 오류 메시지입니다. " +
                "accept-language 헤더가 없거나 메시지가 준비되지 않았다면 영문 메시지를 포함합니다."
        const val DESC_BODY_CODE = "Sandbox Server 의 에러 코드입니다. 클라이언트는 이 값에 따라 오류를 처리할 수 있습니다. " +
                "에러 코드 목록은 ErrorCodeBook 클래스를 참고하시기 바랍니다."
        const val DESC_BODY_DETAILS = "오류 상황을 처리하기 위한 추가 정보입니다. 코드별로 처리 방법은 다를 수 있습니다."

        fun create(message: String, code: String, details: Any?) =
            ErrorResponseV1(Body(message, code, details))
    }
}
