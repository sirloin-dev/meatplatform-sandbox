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
    UNSUPPORTED_CLIENT_VERSION(
        code = "E-00000010", "지원하지 않는 클라이언트 버전. 클라이언트는 이 오류를 수신한 경우, 강제 업데이트 단계로 이행해야 합니다."
    );
}
