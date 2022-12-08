/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.exception

/**
 * Core 로직에서 발생시키는 모든 예외의 최상위 타입입니다.
 *
 * @since 2022-02-14
 */
sealed class SandboxException(
    open val codeBook: ErrorCodeBook,
    override val message: String = "",
    override val cause: Throwable? = null,
) : RuntimeException(message, cause) {
    /**
     * 별도의 message format 규칙이 필요한 예외가 있다면 format 에 필요한 argument 들을 반환해 주세요.
     */
    open val messageArguments: Array<String>? = null

    /**
     * 오류를 표현하기 위한 추가 정보가 필요하다면 이 메소드를 오버라이드 하시기 바랍니다.
     */
    open val details: Any? = null
}

/**
 * 사용측의 잘못되거나, 지원할 수 없는 요청 등을 받았을 때 발생시킬 예외의 상위 타입입니다.
 *
 * @since 2022-02-14
 */
open class ExternalException constructor(
    codeBook: ErrorCodeBook,
    override val message: String = "",
    override val cause: Throwable? = null,
) : SandboxException(codeBook, message, cause)

/**
 * 서비스 내부의 문제로 인해 요청을 처리할 수 없을 때 발생시키는 예외의 상위 타입입니다.
 *
 * @since 2022-02-14
 */
open class InternalException constructor(
    codeBook: ErrorCodeBook,
    override val message: String = "",
    override val cause: Throwable? = null,
) : SandboxException(codeBook, message, cause)
