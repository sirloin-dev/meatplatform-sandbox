/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.small.i18n

import com.github.javafaker.Faker
import com.sirloin.sandbox.server.core.i18n.MessageLocaleSpec
import com.sirloin.sandbox.server.core.i18n.MessageProvider
import com.sirloin.sandbox.server.core.i18n.MessageProviderDefaultImpl
import com.sirloin.sandbox.server.core.i18n.putMessage
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import test.com.sirloin.annotation.SmallTest
import test.com.sirloin.util.text.randomFillChars
import java.util.*
import java.util.stream.Stream

@Suppress("ClassName")  // Test fixture 이름 잘 짓기 위해 무시
@SmallTest
class DefaultMessageProviderSpec {
    private val sut = MessageProviderDefaultImpl

    @DisplayName("Key 에 해당하는 Message Bundle 이 없다면 Placeholder 를 출력한다")
    @Test
    fun `Returns placeholder message if no message bundles for key`() {
        // given:
        val locale = Locale.getAvailableLocales().random()
        val key = Faker().letterify(randomFillChars('?'))

        // when:
        val actual = sut.provide(locale, key)

        // expect:
        assertThat(actual, `is`(MessageProvider.PLACEHOLDER))
    }

    @DisplayName("Key 에 해당하는 Message Bundle 이 있을 때:")
    @Nested
    inner class `Message is returned if bundle is found for key` {
        @Suppress("UNUSED_PARAMETER")   // Test name 표시를 위해 불필요한 argument 선언
        @ParameterizedTest(name = "{0}하는 메시지를 찾을 수 있다")
        @MethodSource("testcase.small.i18n.DefaultMessageProviderSpec#messageBundleFoundUnformatted")
        fun `Returns message for matching locales`(_testName: String, locale: Locale) {
            // when:
            val actual = sut.provide(locale, TEST_KEY)

            // expect:
            assertThat(actual, `is`(TEST_MESSAGE))
        }

        @DisplayName("Locale 이 일치하지 않으면 제일 먼저 입력한 메시지를 출력한다")
        @Test
        fun `Returns the first message if no matching locale is found`() {
            // when:
            val actual = sut.provide(
                // 카자스흐탄-카작어-로마자 (2021년 이전까진 kk-Cyrl-KZ)
                Locale.Builder().setLanguage("kk").setScript("Latn").setRegion("KZ").build(),
                TEST_KEY
            )

            // expect:
            assertThat(actual, not(MessageProvider.PLACEHOLDER))
        }
    }

    @DisplayName("메시지에 추가 Format 이 필요한 경우 :")
    @Nested
    inner class `When formatting is required` {
        @DisplayName("포맷할 값을 주지 않으면 문장을 그대로 출력한다")
        @Test
        fun `Returns raw message when no values are given`() {
            // when:
            val actual = sut.provide(TEST_LOCALE, TEST_KEY)

            // expect:
            assertThat(actual, `is`(TEST_MESSAGE))
        }

        @DisplayName("포맷할 수 없는 값을 주면 문장을 그대로 출력한다")
        @Test
        fun `Returns raw message when illegal values are given`() {
            // when:
            val actual = sut.provide(TEST_LOCALE, TEST_KEY, "NaN")

            // expect:
            assertThat(actual, `is`(TEST_MESSAGE))
        }

        @DisplayName("포맷할 값을 주면 포맷 적용한 문장을 출력한다")
        @Test
        fun `Returns formatted message when values are given`() {
            // when:
            val actual = sut.provide(TEST_LOCALE, TEST_KEY, 100)

            // expect:
            assertThat(actual, both(not(TEST_MESSAGE)).and(not(MessageProvider.PLACEHOLDER)))
        }
    }

    companion object {
        private const val TEST_LANGUAGE = "zh"
        private const val TEST_SCRIPT = "Hans"
        private const val TEST_COUNTRY = "CN"
        private const val TEST_KEY = "TEST_MESSAGE"
        private const val TEST_MESSAGE = "''{0,number}'' is not a valid input."
        private val TEST_LOCALE =
            Locale.Builder().setLanguage(TEST_LANGUAGE).setScript(TEST_SCRIPT).setRegion(TEST_COUNTRY).build()

        private lateinit var originalMessages: Map<String, Map<MessageLocaleSpec, String>>

        @BeforeAll
        @JvmStatic
        fun setupAll() {
            originalMessages = MessageProviderDefaultImpl.messages
            MessageProviderDefaultImpl.messages = HashMap<String, Map<MessageLocaleSpec, String>>().apply {
                put(TEST_KEY, HashMap<MessageLocaleSpec, String>().apply {
                    putMessage(TEST_LOCALE, TEST_MESSAGE)
                })
            }
        }

        @AfterAll
        @JvmStatic
        fun tearDownAll() {
            MessageProviderDefaultImpl.messages = originalMessages
        }

        @JvmStatic
        fun messageBundleFoundUnformatted(): Stream<Arguments> = Stream.of(
            Arguments.of(
                "언어 - 스크립트 - 국가 모두 일치",
                Locale.Builder().setLanguage(TEST_LANGUAGE).setScript(TEST_SCRIPT).setRegion(TEST_COUNTRY).build()
            ),
            Arguments.of(
                "언어 - 국가 모두 일치",
                Locale.Builder().setLanguage(TEST_LANGUAGE).setRegion(TEST_COUNTRY).build()
            ),
            Arguments.of(
                "언어 일치",
                Locale.Builder().setLanguage(TEST_LANGUAGE).build()
            ),
        )
    }
}
