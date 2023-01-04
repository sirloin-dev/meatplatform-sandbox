/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import net.meatplatform.sandbox.util.ToStringHelper

/**
 * @since 2023-05-06
 */
class TestToStringHelper : ToStringHelper {
    private val mapper = ObjectMapper().apply {
        registerModules(SimpleModule(), JavaTimeModule(), KotlinModule.Builder().build())
    }

    override fun toString(value: Any?): String =
        if (value == null) {
            "null"
        } else {
            mapper.writerWithDefaultPrettyPrinter().writeValueAsString(value)
        }
}
