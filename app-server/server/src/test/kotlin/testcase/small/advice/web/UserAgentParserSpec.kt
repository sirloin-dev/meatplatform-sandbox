/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.small.advice.web

import com.sirloin.jvmlib.util.SemanticVersion
import com.sirloin.jvmlib.util.SemanticVersionParser
import io.kotest.matchers.shouldBe
import net.meatplatform.sandbox.advice.web.UserAgentParser
import net.meatplatform.sandbox.advice.web.UserAgentParser.APP_NAME
import net.meatplatform.sandbox.advice.web.UserAgentParser.UA_DELIMITER
import net.meatplatform.sandbox.util.ClientDevicePlatform
import net.meatplatform.sandbox.util.ClientInfo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import test.com.sirloin.annotation.SmallTest
import java.util.*
import java.util.stream.Stream

/**
 * @since 2022-02-14
 */
@SmallTest
internal class UserAgentParserSpec {
    @DisplayName("User-Agent 가 기대하는 패턴과 일치하지 않으면 결과는 null 이다.")
    @ParameterizedTest
    @MethodSource("uaStringNotMatchToPattern")
    fun illegalUserAgentIfNotMatchToPattern(uaString: String) {
        // then:
        val result = UserAgentParser.toClientInfo(uaString)

        // expect:
        result shouldBe null
    }

    @DisplayName("User-Agent 가 기대하는 패턴과 일치하면 ClientInfo 로 바꿀 수 있다.")
    @ParameterizedTest
    @MethodSource("uaStringConvertedToClientInfo")
    fun userAgentParsedToClientInfo(uaString: String, expected: ClientInfo) {
        // then:
        val result = UserAgentParser.toClientInfo(uaString)

        // expect:
        result shouldBe expected
    }

    companion object {
        @JvmStatic
        fun uaStringNotMatchToPattern(): Stream<String> = Stream.of(
            "",
            ";",
            "a/b$UA_DELIMITER c/d e",
            ";;;"
        )

        @JvmStatic
        fun uaStringConvertedToClientInfo(): Stream<Arguments> = Stream.of(
            Arguments.of(
                "$APP_NAME/1.0.0+1$UA_DELIMITER ${ClientDevicePlatform.WEB.code}/ko-KR Asia/Seoul",
                ClientInfo(
                    appName = APP_NAME,
                    appVersion = SemanticVersion(1, 0, 0, "", "1"),
                    devicePlatform = ClientDevicePlatform.WEB,
                    locale = Locale("ko", "KR"),
                    timeZone = TimeZone.getTimeZone("Asia/Seoul")
                )
            ),
            Arguments.of(
                "$APP_NAME/1.0.0-alpha-1+1$UA_DELIMITER ${ClientDevicePlatform.WEB.code}/en-GB Europe/London",
                ClientInfo(
                    appName = APP_NAME,
                    appVersion = SemanticVersion(1, 0, 0, "alpha-1", "1"),
                    devicePlatform = ClientDevicePlatform.WEB,
                    locale = Locale("en", "GB"),
                    timeZone = TimeZone.getTimeZone("Europe/London")
                )
            )
        )
    }
}
