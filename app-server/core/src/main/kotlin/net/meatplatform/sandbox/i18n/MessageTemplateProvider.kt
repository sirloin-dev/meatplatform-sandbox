/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.i18n

import net.meatplatform.sandbox.exception.ErrorCodeBook
import java.util.*

/**
 * @since 2022-02-14
 */
interface MessageTemplateProvider {
    fun provide(locale: Locale, errorCode: ErrorCodeBook): MessageTemplate?
}
