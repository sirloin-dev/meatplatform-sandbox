/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.appConfig

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import net.meatplatform.sandbox.CoreApplication
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import test.InfraTestCoreApplicationImpl

/**
 * @since 2023-05-07
 */
@TestConfiguration
class InfraTestSpringContextConfig {
    @Bean
    fun coreApplication(): CoreApplication {
        return InfraTestCoreApplicationImpl()
    }

    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper().apply {
        registerModules(SimpleModule(), JavaTimeModule(), KotlinModule.Builder().build())
    }
}
