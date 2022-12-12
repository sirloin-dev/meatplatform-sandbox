/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.appconfig

import com.sirloin.jvmlib.util.SemanticVersion
import net.meatplatform.sandbox.util.ClientDevicePlatform

/**
 * @since 2022-12-07
 */
interface ClientVersionConfigHolder {
    val minRequiredVersion: SemanticVersion

    val defaultWebUrl: String

    fun getUpdateUriOf(targetPlatform: ClientDevicePlatform): String
}
