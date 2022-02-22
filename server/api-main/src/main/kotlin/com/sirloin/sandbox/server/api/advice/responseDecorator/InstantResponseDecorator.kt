/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.advice.responseDecorator

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.sirloin.jvmlib.time.truncateToSeconds
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * [Instant] 의 값을 ISO format 에 맞게 초 단위로 자르고, UTC 기준으로 변환합니다.
 * 외부 시스템에서 Microseconds 이하 단위를 유용하게 사용하는 일반적인 경우가 많지 않기 때문입니다.
 *
 * @since 2022-02-14
 */
class InstantResponseDecorator : JsonSerializer<Instant>() {
    override fun serialize(value: Instant?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen?.writeString(
            DateTimeFormatter.ISO_DATE_TIME.format(
                value?.truncateToSeconds()
                    ?.atOffset(ZoneOffset.UTC)
            )
        )
    }
}
