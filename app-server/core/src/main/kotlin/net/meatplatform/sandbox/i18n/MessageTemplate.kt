/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.i18n

import java.text.MessageFormat
import java.util.*

/**
 * @since 2022-06-18
 */
interface MessageTemplate {
    val locale: Locale

    val key: String

    val formattedMessage: String

    /**
     * 만약 [formattedMessage] 에 포맷 지정자가 없거나, [args] 가 `null` 이라면 [formattedMessage]를 그대로 반환합니다.
     * 또한 입력받은 파라미터의 형식이나 길이가 일치하지 않는 경우에도 [formattedMessage]를 그대로 반환합니다.
     *
     * [formattedMessage] 가 포함할 수 있는 포맷 지정자는 [java.text.MessageFormat] 의 규칙을 따릅니다.
     *
     * 또한 포맷 변환에 실패하는 경우, [formattedMessage] 를 그대로 반환합니다.
     * 따라서 예외를 정밀하게 추적해야 한다면 이 메소드를 별도로 구현하시기 바랍니다.
     */
    fun format(vararg args: Any?): String {
        val message = if (args.isEmpty()) {
            formattedMessage
        } else {
            @Suppress("SwallowedException") // 치명적인 오류가 아니기 때문에 예외 전파 하지 않는다.
            try {
                MessageFormat.format(formattedMessage, *args)
            } catch (e: IllegalArgumentException) {
                formattedMessage
            }
        }

        return message.trim()
    }
}
