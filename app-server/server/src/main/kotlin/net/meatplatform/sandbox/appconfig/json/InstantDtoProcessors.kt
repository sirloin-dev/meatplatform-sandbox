/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.appconfig.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.sirloin.jvmlib.time.truncateToSeconds
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * [Instant] 의 값을 ISO format 에 맞게 초 단위로 자르고, UTC 기준으로 변환합니다.
 * 외부 시스템에서 Microseconds 이하 단위를 유용하게 사용하는 일반적인 경우가 많지 않기 때문입니다.
 *
 * @since 2022-02-14
 */
internal class InstantJsonSerializer : JsonSerializer<Instant>() {
    override fun serialize(value: Instant?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen?.writeString(
            value?.atZone(ZoneOffset.UTC)?.format(DateTimeFormatter.ISO_DATE_TIME)
        )
    }
}

/**
 * [Instant] 의 값을 ISO format 에 맞게 초 단위로 자릅니다. 또한 모든 시간 요청은 UTC 기준으로 발생한다고 가정합니다.
 *
 * @since 2022-02-14
 */
internal class InstantJsonDeserializer : JsonDeserializer<Instant>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Instant? {
        return p?.let { ZonedDateTime.parse(it.text, DateTimeFormatter.ISO_DATE_TIME).toInstant().truncateToSeconds() }
    }
}
