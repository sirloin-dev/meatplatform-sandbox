/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.advice

import com.sirloin.jvmlib.util.SemanticVersionParser
import net.meatplatform.sandbox.util.ClientDevicePlatform
import net.meatplatform.sandbox.util.ClientInfo
import java.util.*

/**
 * @since 2022-02-14
 */
internal object UserAgentParser {
    const val UA_DELIMITER = ";"
    const val APP_NAME = "net.meatplatform.sandboxapp"

    private const val PATTERN_SEMVER = "(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)" +
            "(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?" +
            "(?:\\+([0-9a-zA-Z-_]+(?:\\.[0-9a-zA-Z-]+)*))?"

    private val PATTERN_PLATFORM_NAMES = "${ClientDevicePlatform.ANDROID.code}|" +
            "${ClientDevicePlatform.IOS.code}|" +
            "${ClientDevicePlatform.WEB.code}|" +
            "${ClientDevicePlatform.WINDOWS.code}|" +
            "${ClientDevicePlatform.LINUX.code}|" +
            ClientDevicePlatform.MACOS.code
    private const val ISO_639_CHARS = "[a-z]{2}-[A-Z]{2}"
    private const val TIMEZONE_CHARS = ".*"
    private const val UA_GROUP_SIZE = 2

    private val UA_SYNTAX_GROUP0 = Regex("^($APP_NAME)/($PATTERN_SEMVER)")
    private val UA_SYNTAX_GROUP1 = Regex("($PATTERN_PLATFORM_NAMES)/($ISO_639_CHARS) ($TIMEZONE_CHARS)")
    private val UA_SYNTAX = Regex("$UA_SYNTAX_GROUP0$UA_DELIMITER $UA_SYNTAX_GROUP1")

    fun toClientInfo(userAgent: String?): ClientInfo? =
        userAgent?.takeIf { UA_SYNTAX.matches(it) }
            ?.split(UA_DELIMITER)
            ?.map { it.trim() }
            ?.let { (group0, group1) ->
                listOfNotNull(
                    UA_SYNTAX_GROUP0.matchEntire(group0)?.groupValues?.toMutableList()?.also { it.removeFirst() },
                    UA_SYNTAX_GROUP1.matchEntire(group1)?.groupValues?.toMutableList()?.also { it.removeFirst() },
                )
            }
            ?.takeIf { it.size == UA_GROUP_SIZE }
            ?.let { (uaGroup0, uaGroup1) ->
                ClientInfo(
                    appName = uaGroup0[0],
                    appVersion = SemanticVersionParser.toSemanticVersion(uaGroup0[1]),
                    devicePlatform = ClientDevicePlatform.from(uaGroup1[0]),
                    locale = uaGroup1[1].split("-").let { Locale(it[0], it[1]) },
                    timeZone = TimeZone.getTimeZone(uaGroup1[2])
                )
            }
}
