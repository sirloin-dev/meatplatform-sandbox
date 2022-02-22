/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.i18n

import com.sirloin.jvmlib.annotation.VisibleForTesting
import com.sirloin.sandbox.server.core.exception.MtExceptionCode
import java.util.*
import kotlin.collections.HashMap

/**
 * [LocaleProvider.SUPPORTED_LIST] 목록 내의 로케일에 대응하는 메시지 정의 모음.
 *
 * @since 2022-02-14
 */
internal val MESSAGE_RESOURCES = HashMap<String, Map<MessageLocaleSpec, String>>().apply {
    put(MtExceptionCode.WRONG_INPUT.msgKey, LinkedHashMap<MessageLocaleSpec, String>().apply {
        default("''{0}'' is not a valid input.")
        korean("입력값 ''{0}'' 이 올바르지 않습니다.")
    })
    put(MtExceptionCode.SERVICE_NOT_FOUND.msgKey, LinkedHashMap<MessageLocaleSpec, String>().apply {
        default("No suitable service is found for given request.")
        korean("요청을 처리할 서비스가 없습니다.")
    })
    put(MtExceptionCode.WRONG_PRESENTATION.msgKey, LinkedHashMap<MessageLocaleSpec, String>().apply {
        default("No suitable presentation is found for given request.")
        korean("잘못된 처리 결과 표현 요청입니다.")
    })
    put(MtExceptionCode.MALFORMED_INPUT.msgKey, LinkedHashMap<MessageLocaleSpec, String>().apply {
        default("Malformed input.")
        korean("요청 형식이 잘못되었습니다.")
    })
    put(MtExceptionCode.USER_NOT_FOUND.msgKey, LinkedHashMap<MessageLocaleSpec, String>().apply {
        default("User ''{0}'' is not found.")
        korean("이용자 ''{0}'' 를 찾을 수 없습니다.")
    })
    put(MtExceptionCode.UNHANDLED_EXCEPTION.msgKey, LinkedHashMap<MessageLocaleSpec, String>().apply {
        default("The server encountered an unexpected error.")
        korean("요청을 처리하던 중 알 수 없는 오류가 발생했습니다.")
    })
} as Map<String, Map<MessageLocaleSpec, String>>

private fun MutableMap<MessageLocaleSpec, String>.default(message: String) {
    putMessage(LocaleProvider.DEFAULT, message)
}

private fun MutableMap<MessageLocaleSpec, String>.korean(message: String) {
    putMessage(LocaleProvider.SUPPORTED_KOREAN, message)
}

@VisibleForTesting
internal fun MutableMap<MessageLocaleSpec, String>.putMessage(locale: Locale, message: String) {
    // POINT: 여기서 intern 메소드의 의미가 뭘까요?
    val interned = message.intern()

    put(MessageLocaleSpec.createLong(locale), interned)
    put(MessageLocaleSpec.createMedium(locale), interned)
    put(MessageLocaleSpec.createShort(locale), interned)
}
