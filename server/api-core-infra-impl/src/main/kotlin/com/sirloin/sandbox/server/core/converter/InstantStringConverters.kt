/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.converter

import com.sirloin.jvmlib.time.truncateToSeconds
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * Instant 타입 데이터를 문자열 <-> Instant 로 변환할 때 활용합니다.
 * 밀리초 이하 단위를 제거할 때도 사용합니다.
 *
 * @since 2022-02-14
 */
object InstantStringConverters {
    val READ_CONVERTER: Converter<String, Instant> = StringToInstantConverter()
    val WRITE_CONVERTER: Converter<Instant, String> = InstantToStringConverter()
}

@ReadingConverter
private class StringToInstantConverter : Converter<String, Instant> {
    override fun convert(source: String): Instant = Instant.parse(source).truncateToSeconds()
}

@WritingConverter
private class InstantToStringConverter : Converter<Instant, String> {
    override fun convert(source: Instant): String = DateTimeFormatter.ISO_DATE_TIME.format(
        source.truncateToSeconds()
    )
}
