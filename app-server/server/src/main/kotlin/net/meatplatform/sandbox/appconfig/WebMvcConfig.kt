/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.appconfig

import jakarta.servlet.http.HttpServletRequest
import net.meatplatform.sandbox.advice.web.AcceptLanguageLocaleProvider
import net.meatplatform.sandbox.advice.web.UserAgentParserInterceptor
import net.meatplatform.sandbox.i18n.LocaleProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.http.HttpHeaders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * @since 2022-02-14
 */
@Configuration
internal class WebMvcConfig(
    private val userAgentParserInterceptor: UserAgentParserInterceptor
) : WebMvcConfigurer {
    @Bean
    @Scope(WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    fun acceptLanguageLocaleProvider(
        request: HttpServletRequest
    ): LocaleProvider = AcceptLanguageLocaleProvider(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE))

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(userAgentParserInterceptor)
    }
}
