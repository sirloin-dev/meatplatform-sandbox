/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.appconfig.security

import net.meatplatform.sandbox.endpoint.ApiPaths
import net.meatplatform.sandbox.endpoint.v1.ApiPathsV1
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher

/**
 * @since 2022-03-20
 */
@Configuration
class UrlAccessConfig {
    @Bean
    @Qualifier(SpringSecurityConfig.BYPASS_AUTH_URI_LIST)
    fun checkBypassUris(): List<RequestMatcher> = listOf(
        // ERROR - Servlet 내부에서 리다이렉션 등으로 사용할 때가 있으므로 무시해야 함
        ApiPaths.ERROR to null,

        ApiPathsV1.USER to HttpMethod.POST,
        ApiPathsV1.AUTH_LOGIN to HttpMethod.POST
    ).map { (antPath, httpMethod) ->
        return@map if (httpMethod == null) {
            AntPathRequestMatcher(antPath)
        } else {
            AntPathRequestMatcher(antPath, httpMethod.toString())
        }
    }
}
