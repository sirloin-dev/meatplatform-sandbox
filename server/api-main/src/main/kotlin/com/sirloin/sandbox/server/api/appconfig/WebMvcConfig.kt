/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.appconfig

import com.sirloin.sandbox.server.api.advice.AcceptLanguageLocaleProvider
import com.sirloin.sandbox.server.core.i18n.LocaleProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.http.HttpHeaders
import org.springframework.web.context.WebApplicationContext
import javax.servlet.http.HttpServletRequest

/**
 * Spring WebMVC 환경 설정 모음
 *
 * @since 2022-02-14
 */
@Configuration
class WebMvcConfig {
    @Bean
    @Scope(WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    fun acceptLanguageLocaleProvider(
        request: HttpServletRequest
    ): LocaleProvider =
        AcceptLanguageLocaleProvider(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE))
}
