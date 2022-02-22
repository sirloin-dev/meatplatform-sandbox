/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.appconfig

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InjectionPoint
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

/**
 * [org.slf4j.Logger] 를 필요로 하는 Spring component 들에,
 * 매번 새로운 Logger 인스턴스를 생성해서 주입하는 환경 설정
 *
 * 이 설정으로 인해 자동으로 주입받는 [org.slf4j.Logger] 인스턴스는 모두 싱글턴이 아닙니다.
 *
 * @since 2022-02-14
 */
@Configuration
class LoggerConfig {
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun logger(injectionPoint: InjectionPoint): Logger {
        return LoggerFactory.getLogger(
            injectionPoint.methodParameter?.containingClass ?: injectionPoint.field?.declaringClass
        )
    }
}
