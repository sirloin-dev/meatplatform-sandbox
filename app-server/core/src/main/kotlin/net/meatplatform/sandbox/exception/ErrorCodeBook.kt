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
    // region Enterprise exception
    SERVICE_NOT_FOUND(
        code = "E-00000001", "사용자 요청에 해당하는 Backend service 없음. " +
                "잘못된 입력으로 인해 Backend service logic 내의 분기 조건을 만족하지 못한 경우입니다."
    ),
    WRONG_PRESENTATION(
        code = "E-00000002", "요청에 해당하는 Backend service 는 발견했지만, 클라이언트가 요청하는 " +
                "응답 형식 또는 언어를 지원하지 않습니다."
    ),
    WRONG_INPUT(
        code = "E-00000003", "잘못된 입력 발생. 내용은 해독했지만, 문맥이나 타입에 맞지 않는 입력입니다."
    ),
    MALFORMED_INPUT(
        code = "E-00000004", "잘못된 입력 발생. 내용을 해독할 수 없습니다."
    ),
    UNSUPPORTED_CLIENT_VERSION(
        code = "E-00000005", "지원하지 않는 클라이언트 버전. 클라이언트는 이 오류를 수신한 경우, 강제 업데이트 단계로 이행해야 합니다."
    ),
    EXTERNAL_SYSTEM_NOT_RESPONDING(
        code = "E-00000006", "외부 서비스와의 통신에 실패했습니다."
    ),
    AUTHENTICATION_EXPIRED(
        code = "E-00000007", "인증이 만료됨. 클라이언트의 인증 토큰이 만료된 경우 발생합니다. " +
                "accessToken 을 사용했을 때 이 오류가 발생했다면, refreshToken 을 이용해 토큰을 갱신해 주시기 바랍니다. " +
                "하지만 refreshToken 을 사용했을 때 이 오류가 발생한다면 그것은 클라이언트가 지나치게 오랫동안 서비스를 " +
                "사용하지 않아 로그인이 완전히 풀린 상황임을 의미합니다. 이런 상황에서는 로그인을 다시 해야 합니다."
    ),
    // endregion

    // region Application Service exception
    USER_ALREADY_REGISTERED(
        code = "E-00000100", "이미 가입한 이용자입니다."
    ),
    USER_BY_PROVIDER_AUTH_NOT_FOUND(
        code = "E-00000101", "클라이언트가 제공한 로그인 정보에 해당하는 이용자가 없는 경우, 로그인 실패와 함께 발생합니다."
    ),
    EXTERNAL_PROVIDER_AUTH_VERIFICATION_FAILED(
        code = "E-00000102", "제 3자 로그인 서비스에서 인증을 거부했습니다."
    ),
    SUSPICIOUS_IP_ADDRESS_DETECTED(
        code = "E-00000103", "IP 주소 기반의 로그인 정보가 의심스럽습니다."
    ),
    // endregion

    STARTUP_FAILED(
        code = "E-99999998", "알 수 없는 이유로 서버를 실행할 수 없을 때 발생합니다. 클라이언트가 이 오류를 보는 상황은 없어야 합니다."
    ),
    UNHANDLED_EXCEPTION(
        code = "E-99999999", "서버에서 제대로 오류를 처리하지 못했습니다."
    );

    companion object
}
