/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.advice.web

import net.meatplatform.sandbox.i18n.LocaleProvider
import java.util.*

/**
 * 요청의 `Accept-Language` 헤더를 분석해 요청과 가장 유사한 Locale 을 생성하는 [LocaleProvider] 구현체
 * 기본값으로 한국어를 반환
 *
 * @since 2022-02-14
 */
internal class AcceptLanguageLocaleProvider(
    acceptLanguage: String?,
) : LocaleProvider {
    private val _locale: Locale = acceptLanguage?.let {
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

    override val locale: Locale
        get() = _locale

    companion object {
        val DEFAULT: Locale = Locale.KOREAN

        private val SUPPORTED_LIST = listOf(
            Locale.KOREAN,
            Locale.ENGLISH,
        )
    }
}
