/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.exception.external.auth

import net.meatplatform.sandbox.exception.ErrorCodeBook
import net.meatplatform.sandbox.exception.external.WrongInputException

/**
 * @since 2023-05-07
 */
class SuspiciousIpAddressException constructor(
    private val lastIpAddress: String,
    private val lastCountry: String,
    private val lastStateOrRegion: String,
    private val currentIpAddress: String,
    private val currentCountry: String,
    private val currentStateOrRegion: String,
    override val message: String = "Suspicious connection attempted. " +
            "Last connection was from $lastIpAddress ($lastCountry, $lastStateOrRegion), " +
            "but current connection is from $currentIpAddress ($currentCountry, $currentStateOrRegion).",
    override val cause: Throwable? = null
) : WrongInputException(ErrorCodeBook.SUSPICIOUS_IP_ADDRESS_DETECTED, message, cause) {
    override val messageArguments: Array<String> = arrayOf(
        lastIpAddress,
        lastCountry,
        lastStateOrRegion,
        currentIpAddress,
        currentCountry,
        currentStateOrRegion
    )
}
