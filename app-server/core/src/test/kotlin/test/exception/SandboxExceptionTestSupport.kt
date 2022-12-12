/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.exception

import net.meatplatform.sandbox.exception.ErrorCodeBook

fun ErrorCodeBook.Companion.from(code: String): ErrorCodeBook? =
    ErrorCodeBook.values().firstOrNull { it.code == code }
