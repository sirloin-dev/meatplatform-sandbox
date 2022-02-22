/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.endpoint.v1

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import com.fasterxml.jackson.annotation.JsonValue
import com.sirloin.sandbox.server.core.exception.MtException
import java.time.Instant

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

        fun error(exception: MtException) = ErrorResponseV1.create(exception)
    }

    enum class Type(@JsonValue val value: String) {
        OK("OK"),
        ERROR("ERROR");

        companion object {
            @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
            @JvmStatic
            fun from(value: String) = values().firstOrNull { it.value == value }
                ?: throw IllegalArgumentException("Cannot convert '${value}' as Response type")
        }
    }
}
