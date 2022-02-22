/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.exception

/**
 * Business logic 에서 발생한 예외 상황을 코드로 표현합니다.
 * 여기 새 코드를 추가한 경우, MessageProvider 가 코드에 맞는 오류 메시지를 출력하도록 추가 작업을 해야 합니다.
 *
 * @since 2022-02-14
 */
enum class MtExceptionCode(
    val value: Long,
    val msgKey: String
) {
    // region 비즈니스 독립적인 일반 예외 코드 (0x00000001 - 0x0fffffff)
    WRONG_INPUT(value = 0x00000001, msgKey = "ERR_WRONG_INPUT"),
    SERVICE_NOT_FOUND(value = 0x00000002, msgKey = "ERR_SERVICE_NOT_FOUND"),
    WRONG_PRESENTATION(value = 0x00000003, msgKey = "ERR_WRONG_PRESENTATION"),
    MALFORMED_INPUT(value = 0x00000004, msgKey = "ERR_MALFORMED_INPUT"),
    // endregion

    // region 특정 비즈니스 종속 예외 코드 (0x10000000 - 0xfffffffe)
    USER_NOT_FOUND(value = 0x10000000, msgKey = "ERR_USER_NOT_FOUND"),
    // endregion

    // region 미처리 오류
    UNHANDLED_EXCEPTION(value = 0xffffffff, msgKey = "ERR_UNHANDLED_EXCEPTION"),
    UNDEFINED(value = 0x00000000, msgKey = "");
    // endregion

    companion object {
        fun from(value: Long?): MtExceptionCode = values().firstOrNull { it.value == value } ?: UNDEFINED
    }
}
