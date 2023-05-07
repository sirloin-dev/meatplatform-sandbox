/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.appconfig

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import net.meatplatform.sandbox.appconfig.json.InstantJsonDeserializer
import net.meatplatform.sandbox.appconfig.json.InstantJsonSerializer
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Configuration
import java.time.Instant

/**
 * JSON 직렬화에 활용할 추가 처리 로직 모음
 *
 * @since 2022-02-14
 */
@Configuration
internal class JsonCodecConfig(
    private val defaultObjectMapper: ObjectMapper
) : InitializingBean {
    override fun afterPropertiesSet() {
        val simpleModule = SimpleModule()
        simpleModule.addSerializer(Instant::class.java, InstantJsonSerializer())
        simpleModule.addDeserializer(Instant::class.java, InstantJsonDeserializer())

        defaultObjectMapper.registerModules(simpleModule, JavaTimeModule(), KotlinModule.Builder().build())
    }
}
