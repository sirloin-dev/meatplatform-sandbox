/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.advice

import jakarta.servlet.http.HttpServletRequest
import net.meatplatform.sandbox.i18n.LocaleProvider
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import java.util.*

/**
 * 요청의 `Accept-Language` 헤더를 분석해 요청과 가장 유사한 Locale 을 생성하는 [LocaleProvider] 구현체
 * 기본값으로 한국어를 반환
 *
 * 이미 스프링에 동일한 구현이 있으므로 그걸 가져다 쓴다.
 *
 * @since 2022-02-14
 */
internal class AcceptLanguageLocaleProvider : AcceptHeaderLocaleResolver(), LocaleProvider<String?> {
    private lateinit var _locale: Locale

    override fun resolveLocale(request: HttpServletRequest): Locale {
        if (::_locale.isInitialized) {
            return _locale
        }

        _locale = super.resolveLocale(request)
        return _locale
    }

    /*
     * Spring 의 로직을 최대한 쓰되, Spring context 또는 Http Context 밖이라서 Accept-Language 전략 구사가 어려운 경우,
     * 직접 Locale 을 판단한다.
     */
    override fun resolveLocale(context: String?): Locale {
        if (::_locale.isInitialized) {
            return _locale
        }

        _locale = context?.let {
            // Java locale code 는 BCP 47 형식을 따르지 않기 때문에, _(underscore) 를 -(dash) 로 변환
            val maybeBcp47Format = it.replace("_", "-")

            @Suppress("SwallowedException") // 치명적인 오류가 아니기 때문에 예외 전파 하지 않는다.
            return@let try {
                Locale.LanguageRange.parse(maybeBcp47Format)
            } catch (ignore: IllegalArgumentException) {
                null
            }?.run {
                Locale.lookup(this, SUPPORTED_LIST) ?: DEFAULT
            }
        } ?: DEFAULT
        return _locale
    }

    companion object {
        val DEFAULT: Locale = Locale.KOREAN

        private val SUPPORTED_LIST = listOf(
            Locale.KOREAN,
            Locale.ENGLISH,
        )
    }
}
