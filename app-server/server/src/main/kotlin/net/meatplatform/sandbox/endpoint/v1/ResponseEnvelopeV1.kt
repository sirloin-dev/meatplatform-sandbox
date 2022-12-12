/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1

import com.fasterxml.jackson.annotation.JsonPropertyDescription
import com.fasterxml.jackson.annotation.JsonValue
import net.meatplatform.sandbox.util.SerializableEnum
import java.time.Instant

/**
 * @since 2022-02-14
 */
@ResponseV1
abstract class ResponseEnvelopeV1<T>(
    @JsonPropertyDescription(DESC_TYPE)
    open val type: Type,

    @JsonPropertyDescription(DESC_TIMESTAMP)
    open val timestamp: Instant = Instant.now()
) {
    @get:JsonPropertyDescription(DESC_BODY)
    abstract val body: T?

    companion object {
        const val DESC_TYPE = "응답의 유형을 나타냅니다. 'OK' 또는 'ERROR' 로 표시합니다."
        const val DESC_TIMESTAMP = "ISO 8601 표준 포맷으로 나타낸 응답 발생 시간입니다. 시간대는 UTC 기준입니다."
        const val DESC_BODY = "응답의 실제 내용을 담고 있는 객체입니다."

        fun <T> ok(payload: T?) = OkResponseV1(payload)

        fun error(message: String, code: String, details: Any?) = ErrorResponseV1.create(
            message, code, details
        )
    }

    @ResponseV1
    enum class Type(@JsonValue override val code: String) : SerializableEnum<String> {
        @JsonPropertyDescription(DESC_TYPE_OK)
        OK("OK"),

        @JsonPropertyDescription(DESC_TYPE_ERROR)
        ERROR("ERROR");

        companion object {
            const val DESC_TYPE_OK = "응답의 유형을 나타냅니다. 'OK' 또는 'ERROR' 로 표시합니다."
            const val DESC_TYPE_ERROR = "응답의 유형을 나타냅니다. 'OK' 또는 'ERROR' 로 표시합니다."
        }
    }
}
