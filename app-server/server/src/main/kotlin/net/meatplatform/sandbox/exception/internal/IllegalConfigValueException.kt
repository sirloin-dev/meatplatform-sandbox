/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.exception.internal

import net.meatplatform.sandbox.exception.ErrorCodeBook
import net.meatplatform.sandbox.exception.InternalException
import kotlin.reflect.KClass

/**
 * @since 2022-12-30
 */
class IllegalConfigValueException(
    configKey: String,
    configValue: Any,
    expectedType: KClass<*>,
    override val message: String =
        "Config value '$configKey' requires $expectedType, but input value was: $configValue",
    override val cause: Throwable? = null
) : InternalException(ErrorCodeBook.STARTUP_FAILED, message, cause)
