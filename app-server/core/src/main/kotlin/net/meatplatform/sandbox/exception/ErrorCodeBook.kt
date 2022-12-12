/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.exception

import net.meatplatform.sandbox.util.SerializableEnum

/**
 * @since 2022-02-14
 */
enum class ErrorCodeBook(
    override val code: String,
    /** 자동 문서화에 활용하는 field 입니다. 실제 로직에서 활용하지 마세요. */
    val description: String
) : SerializableEnum<String> {
    SERVICE_NOT_FOUND(
        code = "E-00000001", "사용자 요청에 해당하는 Backend service 없음. " +
                "잘못된 입력으로 인해 Backend service logic 내의 분기 조건을 만족하지 못한 경우 발생합니다."
    ),
    WRONG_PRESENTATION(
        code = "E-00000002", "요청에 해당하는 Backend service 는 발견했지만, 클라이언트가 요청하는 " +
                "응답 형식 또는 언어를 지원하지 못하는 경우."
    ),
    WRONG_INPUT(
        code = "E-00000007", "잘못된 입력 발생. 내용은 해독했지만, 문맥이나 타입에 맞지 않는 입력을 받았을 때 발생합니다."
    ),
    MALFORMED_INPUT(
        code = "E-00000008", "잘못된 입력 발생. 내용을 아예 해독할 수 없을 경우 발생합니다."
    ),
    UNSUPPORTED_CLIENT_VERSION(
        code = "E-00000010", "지원하지 않는 클라이언트 버전. 클라이언트는 이 오류를 수신한 경우, 강제 업데이트 단계로 이행해야 합니다."
    ),
    USER_ALREADY_REGISTERED(
        code = "E-00000020", "지원하지 않는 클라이언트 버전. 클라이언트는 이 오류를 수신한 경우, 강제 업데이트 단계로 이행해야 합니다."
    ),
    UNHANDLED_EXCEPTION(
        code = "E-99999999", "서버에서 오류 처리를 잘못한 경우 발생합니다."
    );

    companion object
}
