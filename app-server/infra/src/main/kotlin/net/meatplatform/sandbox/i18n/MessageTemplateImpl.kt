/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.i18n

import java.util.*

/**
 * @since 2022-02-14
 */
internal data class MessageTemplateImpl(
    override val locale: Locale,
    override val key: String,
    override val formattedMessage: String
) : MessageTemplate
