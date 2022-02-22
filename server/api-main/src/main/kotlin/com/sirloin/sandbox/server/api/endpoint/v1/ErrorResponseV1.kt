/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.endpoint.v1

import com.fasterxml.jackson.annotation.JsonPropertyDescription
import com.sirloin.sandbox.server.core.exception.MtException

data class ErrorResponseV1(override val body: Body) : ResponseEnvelopeV1<ErrorResponseV1.Body>(Type.ERROR) {
    data class Body(
        @JsonPropertyDescription(DESC_BODY_MESSAGE)
        val message: String,

        @JsonPropertyDescription(DESC_BODY_CODE)
        val code: String
    )

    companion object {
        private const val CODE_LENGTH = "8"

        const val DESC_BODY_MESSAGE = "클라이언트의 accept-language 헤더에 맞는 언어로 출력한 오류 메시지입니다. " +
                "accept-language 헤더가 없거나 메시지가 준비되지 않았다면 영문 메시지를 포함합니다."
        const val DESC_BODY_CODE = "${CODE_LENGTH}자리 Hex 문자열 에러 코드입니다. " +
                "클라이언트는 이 값에 따라 오류를 처리할 수 있습니다. " +
                "에러 코드 목록은 MtExceptionCode 를 참고하시기 바랍니다."

        private const val HEX_CODE_FORMAT = "0x%0${CODE_LENGTH}x"

        fun create(exception: MtException) = ErrorResponseV1(
            Body(exception.message ?: "", HEX_CODE_FORMAT.format(exception.code.value))
        )
    }
}
