/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.i18n

import java.util.*

/**
 * 현재 처리할 요청의 문맥에 맞는 Locale 정보를 반환합니다.
 *
 * @since 2022-02-14
 */
interface LocaleProvider<T> {
    fun resolveLocale(context: T): Locale
}
