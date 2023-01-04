/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication

/**
 * @since 2022-12-09
 */
@Converter
class ProviderAuthenticationTypeConverter : AttributeConverter<ProviderAuthentication.Type, String> {
    override fun convertToDatabaseColumn(attribute: ProviderAuthentication.Type?): String? = attribute?.code

    override fun convertToEntityAttribute(dbData: String?): ProviderAuthentication.Type? =
        ProviderAuthentication.Type.from(dbData)
}
