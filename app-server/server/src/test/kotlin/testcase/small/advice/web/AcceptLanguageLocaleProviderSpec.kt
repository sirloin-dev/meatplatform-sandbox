/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.small.advice.web

import net.meatplatform.sandbox.advice.AcceptLanguageLocaleProvider
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import test.com.sirloin.annotation.SmallTest
import test.util.randomAlphanumeric
import java.util.*
import java.util.stream.Stream

/**
 * @since 2022-02-14
 */
@SmallTest
internal class AcceptLanguageLocaleProviderSpec {
    @Nested
    @DisplayName("Accept-Language 가 잘못된 경우:")
    inner class WhenAcceptLanguageIsWrong {
        @ParameterizedTest(name = "{index}: ''{0}'' 일때 로케일은 AcceptLanguageLocaleProvider.DEFAULT 가 된다")
        @MethodSource("testcase.small.advice.web.AcceptLanguageLocaleProviderSpec#acceptLanguageNullOrEmptyParams")
        fun acceptLanguageIsEmpty(acceptLanguage: String?) {
            // given:
            val sut = initSut()

            // expect:
            assertThat(sut.resolveLocale(acceptLanguage), `is`(AcceptLanguageLocaleProvider.DEFAULT))
        }

        @DisplayName("이상한 문자열일때, 로케일은 AcceptLanguageLocaleProvider.DEFAULT 가 된다")
        @Test
        fun acceptLanguageIsIllegal() {
            // given:
            val acceptLanguage = randomAlphanumeric()

            // then:
            val result = initSut().resolveLocale(acceptLanguage)

            // expect:
            assertThat(result, `is`(AcceptLanguageLocaleProvider.DEFAULT))
        }
    }

    @Nested
    @DisplayName("Accept-Language 가 있는 경우:")
    inner class WhenAcceptLanguageIsAcceptable {
        @DisplayName("지원 목록에 없는 로케일인 경우 로케일은 AcceptLanguageLocaleProvider.DEFAULT 가 된다")
        @Test
        fun acceptLanguageIsUnsupported() {
            // given:
            val acceptLanguage = "nl"   // 네덜란드

            // then:
            val result = initSut().resolveLocale(acceptLanguage)

            // expect:
            assertThat(result, `is`(AcceptLanguageLocaleProvider.DEFAULT))
        }

        @DisplayName("Weight 가 가장 높은 로케일을 선택한다")
        @Test
        fun highestWeghtIsSelected() {
            // given:
            val acceptLanguage = "ko, en-GB;q=0.8, en;q=0.7"

            // then:
            val result = initSut().resolveLocale(acceptLanguage)

            // expect:
            assertThat(result, `is`(Locale.KOREAN))
        }

        @DisplayName("비슷한 언어를 선택한다")
        @Test
        fun similarLocaleIsSelected() {
            // given:
            val acceptLanguage = "en-GB"

            // then:
            val result = initSut().resolveLocale(acceptLanguage)

            // expect:
            assertThat(result, `is`(Locale.ENGLISH))
        }
    }

    private fun initSut() = AcceptLanguageLocaleProvider()

    companion object {
        @JvmStatic
        fun acceptLanguageNullOrEmptyParams(): Stream<String> =
            Stream.of(null, "")
    }
}
