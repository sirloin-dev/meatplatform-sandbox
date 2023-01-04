/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.util

import net.meatplatform.sandbox.CoreApplication

/**
 * @since 2023-05-06
 */
interface ToStringHelper {
    fun toString(value: Any?): String

    companion object {
        fun toString(value: Any?): String = CoreApplication.instance.toStringHelper().toString(value)
    }
}
