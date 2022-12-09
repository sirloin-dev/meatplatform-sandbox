/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.converter

import com.sirloin.jvmlib.util.toByteArray
import com.sirloin.jvmlib.util.toUUID
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.util.*

/**
 * Uuid 를 16 바이트 길이의 ByteArray 로, 또 반대로 변환합니다.
 * Database 저장 또는 전송 로직에 활용할 수 있습니다.
 *
 * @since 2022-02-14
 */
@Converter
class UuidConverter : AttributeConverter<UUID, ByteArray> {
    override fun convertToDatabaseColumn(attribute: UUID?): ByteArray? = attribute?.toByteArray()

    override fun convertToEntityAttribute(dbData: ByteArray?): UUID? = dbData?.toUUID()
}
