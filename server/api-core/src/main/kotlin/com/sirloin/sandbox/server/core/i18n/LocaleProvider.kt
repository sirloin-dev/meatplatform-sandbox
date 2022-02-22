/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.i18n

import java.util.*

/**
 * 현재 처리할 요청의 문맥에 맞는 Locale 정보를 반환합니다.
 * 만약 적절한 Locale 정보를 찾지 못한다면, [DEFAULT] 로케일을 반환합니다.
 *
 * @since 2022-02-14
 */
interface LocaleProvider {
    val locale: Locale

    companion object {
        val DEFAULT: Locale = Locale.ENGLISH

        /*
         * ISO 638-1 언어 코드 목록
         * https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
         *
         * 언어 스크립트 코드 목록
         * http://unicode.org/iso15924/iso15924-codes.html
         *
         * ISO 3166 ALPHA-2 국가 코드 목록
         * https://www.iso.org/obp/ui/#search/code/
         */
        val SUPPORTED_KOREAN: Locale = Locale.KOREAN

        val SUPPORTED_LIST: List<Locale> = listOf(
            DEFAULT,
            SUPPORTED_KOREAN,
        )

        /**
         * 지원 locale 에 가장 근접한 locale 을 찾습니다. 만약 없다면, DEFAULT 를 반환합니다.
         */
        fun matchSupportedLocale(ranges: List<Locale.LanguageRange>): Locale =
            Locale.lookup(ranges, SUPPORTED_LIST) ?: DEFAULT

        fun defaultInstance(): LocaleProvider = DefaultLocaleProviderImpl
    }
}

private object DefaultLocaleProviderImpl : LocaleProvider {
    override val locale: Locale = LocaleProvider.DEFAULT
}
