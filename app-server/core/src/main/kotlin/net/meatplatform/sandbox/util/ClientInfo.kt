/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.util

import com.sirloin.jvmlib.util.SemanticVersion
import java.util.*

/**
 * @since 2022-02-14
 */
data class ClientInfo(
    val appName: String,
    val appVersion: SemanticVersion?,
    val devicePlatform: ClientDevicePlatform?,
    val locale: Locale?,
    val timeZone: TimeZone?
)

