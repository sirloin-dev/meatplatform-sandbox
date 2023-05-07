/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.appConfig

import com.sirloin.jvmlib.util.SemanticVersion
import net.meatplatform.sandbox.appconfig.ClientVersionConfigHolder
import net.meatplatform.sandbox.util.ClientDevicePlatform

/**
 * @since 2023-05-07
 */
class TestClientVersionConfigHolder : ClientVersionConfigHolder {
    override val minRequiredVersion: SemanticVersion = SemanticVersion(1, 0, 0)

    override val defaultWebUrl: String = "https://github.com/sirloin-dev/meatplatform"

    override fun getUpdateUriOf(targetPlatform: ClientDevicePlatform): String = defaultWebUrl
}
