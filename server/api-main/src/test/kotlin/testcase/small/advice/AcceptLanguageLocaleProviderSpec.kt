/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.small.advice

import com.github.javafaker.Faker
import com.sirloin.sandbox.server.api.advice.AcceptLanguageLocaleProvider
import com.sirloin.sandbox.server.core.i18n.LocaleProvider
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import test.com.sirloin.annotation.SmallTest
import test.com.sirloin.util.text.randomFillChars
import java.util.*
import java.util.stream.Stream

@SmallTest
@Suppress("ClassName")  // Test fixture 이름 잘 짓기 위해 무시
class AcceptLanguageLocaleProviderSpec {
    @Nested
    @DisplayName("Accept-Language 가 잘못된 경우:")
    inner class `When Accept-Language is wrong` {
        @ParameterizedTest(name = "{index}: ''{0}'' 일때 로케일은 LocaleProvider.DEFAULT 가 된다")
        @MethodSource("testcase.small.advice.AcceptLanguageLocaleProviderSpec#acceptLanguageNullOrEmptyParams")
        fun `Empty Accept-Language`(acceptLanguage: String?) {
            // given:
            val sut = initSut(acceptLanguage)

            // expect:
            assertThat(sut.locale, `is`(LocaleProvider.DEFAULT))
        }

        @DisplayName("이상한 문자열일때, 로케일은 LocaleProvider.DEFAULT 가 된다")
        @Test
        fun `Illegal Accept-Language`() {
            // given:
            val sut = initSut(Faker().letterify(randomFillChars('?')))

            // expect:
            assertThat(sut.locale, `is`(LocaleProvider.DEFAULT))
        }
    }

    @Nested
    @DisplayName("Accept-Language 가 있는 경우:")
    inner class `When Accept-Language is acceptable` {
        @DisplayName("지원 목록에 없는 로케일인 경우 로케일은 LocaleProvider.DEFAULT 가 된다")
        @Test
        fun `Unsupported Accept-Language`() {
            // given:
            val sut = initSut("nl")   // 네덜란드

            // expect:
            assertThat(sut.locale, `is`(LocaleProvider.DEFAULT))
        }

        @DisplayName("Weight 가 가장 높은 로케일을 선택한다")
        @Test
        fun `Highest weight is selected`() {
            // given:
            val sut = initSut("ko, en-GB;q=0.8, en;q=0.7")

            // expect:
            assertThat(sut.locale, `is`(Locale.KOREAN))
        }

        @DisplayName("비슷한 언어를 선택한다")
        @Test
        fun `Similar locale is selected`() {
            // given:
            val sut = initSut("en-GB")

            // expect:
            assertThat(sut.locale, `is`(Locale.ENGLISH))
        }
    }

    private fun initSut(acceptLanguage: String?) = AcceptLanguageLocaleProvider(acceptLanguage)

    companion object {
        @JvmStatic
        fun acceptLanguageNullOrEmptyParams(): Stream<String> =
            Stream.of(null, "")
    }
}
