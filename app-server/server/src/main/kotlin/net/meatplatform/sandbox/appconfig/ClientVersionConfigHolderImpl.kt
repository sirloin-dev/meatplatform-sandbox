/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.appconfig

import com.sirloin.jvmlib.util.SemanticVersion
import com.sirloin.jvmlib.util.SemanticVersionParser
import net.meatplatform.sandbox.domain.auth.ClientDevicePlatform
import org.slf4j.Logger
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.util.*

/**
 * @since 2022-12-07
 */
@Configuration
internal class ClientVersionConfigHolderImpl(
    private val log: Logger
) : ClientVersionConfigHolder, InitializingBean {
    @Value("\${$PROPS_MIN_VERSION}")
    private lateinit var minVersion: String
    private lateinit var _minRequiredVersion: SemanticVersion

    @Value("\${$PROPS_UPDATE_URI_ANDROID:}")
    private lateinit var _updateUriAndroid: String

    @Value("\${$PROPS_UPDATE_URI_IOS:}")
    private lateinit var _updateUriIos: String

    @Value("\${$PROPS_UPDATE_URI_WEB:}")
    private lateinit var _updateUriWeb: String

    @Value("\${$PROPS_UPDATE_URI_WINDOWS:}")
    private lateinit var _updateUriWindows: String

    @Value("\${$PROPS_UPDATE_URI_LINUX:}")
    private lateinit var _updateUriLinux: String

    @Value("\${$PROPS_UPDATE_URI_MACOS:}")
    private lateinit var _updateUriMacos: String

    private val updateUri = EnumMap<ClientDevicePlatform, String>(ClientDevicePlatform::class.java)

    override val minRequiredVersion: SemanticVersion
        get() = _minRequiredVersion

    @Value("\${$PROPS_DEFAULT_WEB_URL}")
    override lateinit var defaultWebUrl: String

    override fun getUpdateUriOf(targetPlatform: ClientDevicePlatform): String =
        updateUri[targetPlatform] ?: ""

    override fun afterPropertiesSet() {
        var errorCount = 0

        if (::minVersion.isInitialized) {
            val maybeVersion = SemanticVersionParser.toSemanticVersion(minVersion)
            if (maybeVersion == null) {
                log.error("$PROPS_MIN_VERSION: '{}'", minVersion)
                ++errorCount
            } else {
                _minRequiredVersion = maybeVersion
                log.trace("$PROPS_MIN_VERSION: '{}' -> {}", minVersion, minRequiredVersion)
            }
        } else {
            log.error("$PROPS_MIN_VERSION 설정이 없습니다.")
            ++errorCount
        }

        updateUri[ClientDevicePlatform.ANDROID] = _updateUriAndroid
        updateUri[ClientDevicePlatform.IOS] = _updateUriIos
        updateUri[ClientDevicePlatform.WEB] = _updateUriWeb
        updateUri[ClientDevicePlatform.WINDOWS] = _updateUriWindows
        updateUri[ClientDevicePlatform.LINUX] = _updateUriLinux
        updateUri[ClientDevicePlatform.MACOS] = _updateUriMacos

        if (log.isTraceEnabled) {
            val logPairs = listOf(
                PROPS_UPDATE_URI_ANDROID to ClientDevicePlatform.ANDROID,
                PROPS_UPDATE_URI_IOS to ClientDevicePlatform.IOS,
                PROPS_UPDATE_URI_WEB to ClientDevicePlatform.WEB,
                PROPS_UPDATE_URI_WINDOWS to ClientDevicePlatform.WINDOWS,
                PROPS_UPDATE_URI_LINUX to ClientDevicePlatform.LINUX,
                PROPS_UPDATE_URI_MACOS to ClientDevicePlatform.MACOS
            )

            logPairs.forEach { log.trace("${it.first}: '{}'", getUpdateUriOf(it.second)) }
        }

        if (errorCount > 0) {
            throw AssertionError("$PROPS_PREFIX 설정에 하나 이상의 문제가 있습니다. 오류 로그를 확인하세요.")
        }
    }

    companion object {
        private const val PROPS_PREFIX = "sandboxapp.client"
        private const val PROPS_MIN_VERSION = "$PROPS_PREFIX.minVersion"
        private const val PROPS_DEFAULT_WEB_URL = "$PROPS_PREFIX.defaultWebUrl"

        private const val PROPS_UPDATE_URI_PREFIX = "$PROPS_PREFIX.updateUri"
        private const val PROPS_UPDATE_URI_ANDROID = "$PROPS_UPDATE_URI_PREFIX.android"
        private const val PROPS_UPDATE_URI_IOS = "$PROPS_UPDATE_URI_PREFIX.ios"
        private const val PROPS_UPDATE_URI_WEB = "$PROPS_UPDATE_URI_PREFIX.web"
        private const val PROPS_UPDATE_URI_WINDOWS = "$PROPS_UPDATE_URI_PREFIX.windows"
        private const val PROPS_UPDATE_URI_LINUX = "$PROPS_UPDATE_URI_PREFIX.linux"
        private const val PROPS_UPDATE_URI_MACOS = "$PROPS_UPDATE_URI_PREFIX.macos"
    }
}
