/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.small.responseDecorator

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.sirloin.sandbox.server.api.advice.responseDecorator.InstantResponseDecorator
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import test.com.sirloin.annotation.SmallTest
import java.time.Instant

@ExtendWith(MockitoExtension::class)
@SmallTest
class InstantResponseDecoratorSpec {
    private lateinit var sut: InstantResponseDecorator

    @Mock
    private lateinit var mockGen: JsonGenerator

    @Mock
    private lateinit var serializers: SerializerProvider

    @BeforeEach
    fun setUp() {
        sut = InstantResponseDecorator()
    }

    @DisplayName("0초일때 초 단위를 누락하지 않아야 한다")
    @Test
    fun `Zero values should never be truncated`() {
        // given:
        val value = Instant.parse("2022-01-01T00:00:00Z")

        // when:
        sut.serialize(value, mockGen, serializers)

        // then:
        val argCaptor: ArgumentCaptor<String> = ArgumentCaptor.forClass(String::class.java)
        verify(mockGen).writeString(argCaptor.capture())

        // expect:
        assertThat(argCaptor.value, `is`("2022-01-01T00:00:00Z"))
    }

    @DisplayName("Time zone 을 UTC 기준으로 변환한다")
    @Test
    fun `Time zone is converted to UTC`() {
        // given:
        val value = Instant.parse("2022-01-01T09:00:00+09:00")

        // when:
        sut.serialize(value, mockGen, serializers)

        // then:
        val argCaptor: ArgumentCaptor<String> = ArgumentCaptor.forClass(String::class.java)
        verify(mockGen).writeString(argCaptor.capture())

        // expect:
        assertThat(argCaptor.value, `is`("2022-01-01T00:00:00Z"))
    }
}
