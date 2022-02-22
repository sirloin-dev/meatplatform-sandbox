/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.converter

import com.sirloin.jvmlib.util.toByteArray
import com.sirloin.jvmlib.util.toUUID
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.util.*

/**
 * Uuid 를 16 바이트 길이의 ByteArray 로, 또 반대로 변환합니다.
 * Database 저장 또는 전송 로직에 활용할 수 있습니다.
 *
 * @since 2022-02-14
 */
object BytesUuidConverters {
    val READ_CONVERTER: Converter<ByteArray, UUID> = BytesToUuidConverter()
    val WRITE_CONVERTER: Converter<UUID, ByteArray> = UuidToBytesConverter()
}

@ReadingConverter
private class BytesToUuidConverter : Converter<ByteArray, UUID> {
    override fun convert(source: ByteArray): UUID = source.toUUID()
}

@WritingConverter
private class UuidToBytesConverter : Converter<UUID, ByteArray> {
    override fun convert(source: UUID): ByteArray = source.toByteArray()
}
