/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.i18n

import net.meatplatform.sandbox.exception.ErrorCodeBook
import org.springframework.stereotype.Component
import java.util.*

/**
 * 나중에 필요하면 메시지를 외부화 하도록 합니다.
 *
 * @since 2022-02-14
 */
@Component
internal class MessageTemplateProviderImpl : MessageTemplateProvider {
    private val messages = HashMap<Locale, EnumMap<ErrorCodeBook, String>>()

    override fun provide(locale: Locale, errorCode: ErrorCodeBook): MessageTemplate? =
        when (locale.language) {
            "ko" -> errorCode.asKoreanMessageTemplate()
            else -> null
        }

    private fun ErrorCodeBook.asKoreanMessageTemplate(): MessageTemplate = MessageTemplateImpl(
        locale = Locale.KOREAN,
        key = this.name,
        formattedMessage = when (this) {
            ErrorCodeBook.SERVICE_NOT_FOUND -> "요청을 처리할 서비스가 없습니다."
            ErrorCodeBook.WRONG_PRESENTATION -> "요청한 형태로 결과를 표현할 수 없습니다."
            ErrorCodeBook.WRONG_INPUT -> "''{0}'' 은 잘못된 입력입니다."
            ErrorCodeBook.MALFORMED_INPUT -> "요청 형식이 잘못되었습니다."
            ErrorCodeBook.UNSUPPORTED_CLIENT_VERSION -> "최소 버전 {0} 이상의 클라이언트를 이용하세요."
            ErrorCodeBook.USER_ALREADY_REGISTERED -> "''{0}'' 은 이미 등록한 이메일 주소입니다."
            ErrorCodeBook.UNHANDLED_EXCEPTION -> "알 수 없는 오류가 발생했습니다."
        }
    )
}
