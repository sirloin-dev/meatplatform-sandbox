/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.util

import com.sirloin.jvmlib.crypto.toSha256Bytes
import com.sirloin.jvmlib.text.toHexString

/**
 * @since 2022-12-09
 */
interface PasswordCodecMixin {
    fun encodePassword(password: String): String = password.toSha256Bytes().toHexString()
}
