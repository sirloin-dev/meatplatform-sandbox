/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.exception.external

import com.sirloin.jvmlib.util.SemanticVersion
import net.meatplatform.sandbox.exception.ErrorCodeBook
import net.meatplatform.sandbox.exception.ExternalException

/**
 * @since 2022-12-06
 */
internal class UnsupportedClientVersionException(
    private val requiredVersion: SemanticVersion,
    updateUri: String,
    override val message: String = "Minimum version $requiredVersion is required.",
    override val cause: Throwable? = null
) : ExternalException(ErrorCodeBook.UNSUPPORTED_CLIENT_VERSION, message, cause) {
    override val messageArguments: Array<String> = arrayOf(requiredVersion.toString())

    override val details: Any = UpdateInfo(requiredVersion.toString(), updateUri)

    /**
     * 지원되지 않는 클라이언트 버전 예외 발생시,
     * ClientException 클래스의 details 파라미터에 하나의 데이터 오브젝트로 넣어주기 위해
     * [UpdateInfo] 클래스를 정의한다.
     */
    internal data class UpdateInfo(
        val requiredVersion: String,
        val updateUri: String
    )
}
