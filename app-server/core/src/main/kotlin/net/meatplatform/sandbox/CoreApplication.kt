/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox

import net.meatplatform.sandbox.util.ToStringHelper

/**
 * @since 2023-05-06
 */
interface CoreApplication {
    fun toStringHelper(): ToStringHelper

    companion object {
        private var _instance: CoreApplication? = null

        val instance: CoreApplication
            get() = _instance.let {
                if (it == null) {
                    throw IllegalStateException("CoreApplication is not initialized")
                }

                return it
            }

        fun init(instance: CoreApplication): CoreApplication {
            if (_instance == null) {
                _instance = instance
                return instance
            } else {
                throw IllegalStateException("CoreApplication is already initialized")
            }
        }

        fun close() {
            _instance = null
        }
    }
}
