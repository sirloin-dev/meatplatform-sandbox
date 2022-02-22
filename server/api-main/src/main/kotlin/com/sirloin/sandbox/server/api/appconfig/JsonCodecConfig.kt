/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.appconfig

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.sirloin.sandbox.server.api.advice.responseDecorator.InstantResponseDecorator
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Configuration
import java.time.Instant

/**
 * JSON 직렬화에 활용할 추가 처리 로직 모음
 *
 * @since 2022-02-14
 */
@Configuration
class JsonCodecConfig(
    private val defaultObjectMapper: ObjectMapper
) : InitializingBean {
    override fun afterPropertiesSet() {
        val simpleModule = SimpleModule()
        simpleModule.addSerializer(Instant::class.java, InstantResponseDecorator())

        defaultObjectMapper.registerModules(simpleModule);
    }
}
