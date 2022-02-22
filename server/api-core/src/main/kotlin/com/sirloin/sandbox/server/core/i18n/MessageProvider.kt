/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.i18n

import com.sirloin.jvmlib.annotation.VisibleForTesting
import com.sirloin.sandbox.server.core.i18n.MessageProvider.Companion.PLACEHOLDER
import java.text.MessageFormat
import java.util.*

/**
 * 이용자의 문맥에 일치하거나, 최대한 근접한 로케일로 작성한 문장을 출력할 수 있도록 돕는 로직.
 * `key` 에 해당하는 문장이 있을 경우, 다음의 순서대로 문장을 찾습니다.
 *
 * 1. 언어-표기문자-국가 모두 일치하는 문장
 * 2. 언어-국가 모두 일치하는 문장
 * 3. 국가 일치하는 문장
 * 4. 로케일 구분 없이 입력 순서의 가장 첫 번째 문장
 *
 * `key` 에 해당하는 문장이 없을 때는, [PLACEHOLDER] 를 출력합니다.
 *
 * @since 2022-02-14
 */
interface MessageProvider {
    fun provide(locale: Locale, key: String, vararg args: Any?): String

    companion object {
        const val PLACEHOLDER = "Failed to process this request."

        fun hardcodedInstance(): MessageProvider = MessageProviderDefaultImpl
    }
}

internal object MessageProviderDefaultImpl : MessageProvider {
    override fun provide(locale: Locale, key: String, vararg args: Any?): String {
        val msgCandidates = messages[key]
        if (msgCandidates.isNullOrEmpty()) {
            return MessageProvider.PLACEHOLDER
        }

        val targetLocales = listOf(locale, LocaleProvider.DEFAULT)

        var message: String? = null
        for (i in targetLocales.indices) {
            val targetLocale = targetLocales[i]

            // POINT: 여기서 Medium, Short 를 검사하지 않는 이유는 뭘까요?
            message = msgCandidates[MessageLocaleSpec.createLong(targetLocale)]

            if (message != null) {
                break
            }
        }

        return (if (message == null) {
            msgCandidates[msgCandidates.keys.first()]!!
        } else {
            if (args.isEmpty()) {
                message
            } else {
                @Suppress("SwallowedException") // 치명적인 오류가 아니기 때문에 예외 전파 하지 않는다.
                try {
                    MessageFormat.format(message, *args)
                } catch (e: IllegalArgumentException) {
                    message
                }
            }
        }).trim()
    }

    @VisibleForTesting
    internal var messages = MESSAGE_RESOURCES
}

internal data class MessageLocaleSpec(
    val language: String,
    val script: String = "",
    val country: String = ""
) {
    companion object {
        fun createLong(locale: Locale) = MessageLocaleSpec(
            language = locale.language,
            script = locale.script,
            country = locale.country
        )

        fun createMedium(locale: Locale) = MessageLocaleSpec(
            language = locale.language,
            country = locale.country
        )

        fun createShort(locale: Locale) = MessageLocaleSpec(
            language = locale.language
        )
    }
}
