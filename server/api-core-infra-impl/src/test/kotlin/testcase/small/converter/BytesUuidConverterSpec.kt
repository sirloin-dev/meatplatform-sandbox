/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.small.converter

import com.sirloin.sandbox.server.core.converter.BytesUuidConverters
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import test.com.sirloin.annotation.SmallTest
import java.util.*

@SmallTest
class BytesUuidConverterSpec {
    @DisplayName("UUID 를 Byte array 로, 결과를 다시 UUID 로 변환할 수 있다")
    @Test
    fun `Can convert UUID as ByteArray and vice versa`() {
        // given:
        val uuidToByteArray = BytesUuidConverters.WRITE_CONVERTER
        val byteArrayToUuid = BytesUuidConverters.READ_CONVERTER

        // when:
        val uuid = UUID.randomUUID()

        // then:
        val byteArray = uuidToByteArray.convert(uuid)
        val maybeUuid = byteArrayToUuid.convert(byteArray!!)

        // expect:
        assertThat(maybeUuid, `is`(uuid))
    }
}
