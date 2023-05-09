/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.appconfig.security

import net.meatplatform.sandbox.exception.internal.IllegalConfigValueException
import net.meatplatform.sandbox.security.AuthorizationHeaderFilter
import net.meatplatform.sandbox.security.CorsFilter
import net.meatplatform.sandbox.security.authentication.JwtAuthenticationProvider
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.channel.ChannelProcessingFilter
import org.springframework.security.web.context.SecurityContextHolderFilter
import org.springframework.security.web.util.matcher.RequestMatcher

/**
 * @since 2022-03-20
 */
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
class SpringSecurityConfig(
) {
    @Bean
    fun filterChain(
        http: HttpSecurity,
        @Qualifier(BYPASS_AUTH_URI_LIST) securityBypassRequestMatchers: List<RequestMatcher>,
        jwtAuthenticationProvider: JwtAuthenticationProvider
    ): SecurityFilterChain {
        http
            .addFilterBefore(CorsFilter(), ChannelProcessingFilter::class.java)
            .addFilterAfter(
                AuthorizationHeaderFilter(
                    securityBypassRequestMatchers,
                    LoggerFactory.getLogger(AuthorizationHeaderFilter::class.java)
                ),
                SecurityContextHolderFilter::class.java
            )
            .cors().disable()       // CORS 정책 직접 핸들링
            .csrf().disable()       // Web Application 에서의 Session 기반 Login 을 사용하지 않으므로 CSRF 방어 설정을 하지 않는다.
            .anonymous().disable()  // 일단 모든 Endpoint 를 secure 하고 bypass 전략을 직접 구현하도록 한다.
            .formLogin().disable()  // REST API 라서 FORM 방식의 로그인을 사용하지 않는다.
            .logout().disable()     // Bearer Authentication Token 에 있는 만료 시간을 사용해 로그아웃을 구현한다.
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
            .requestMatchers(*securityBypassRequestMatchers.toTypedArray()).permitAll()
            .anyRequest().fullyAuthenticated()
            .and()
            // Spring Security 의 AuthenticationManager 가 JwtAuthenticationProvider 를 인증 과정에 사용하도록 설정
            .oauth2ResourceServer { it.authenticationManagerResolver(jwtAuthenticationProvider) }

        return http.build()
    }

    @Bean
    fun authenticationPolicy(
        @Value("\${$CONFIG_AUTH_TOKEN_ISSUER}") issuerName: String,
        @Value("\${$CONFIG_AUTH_TOKEN_AUDIENCE}") audience: String,
        @Value("\${$CONFIG_AUTH_TOKEN_VALIDATION_CACHE_COUNT}") validationCacheCount: String,
        @Value("\${$CONFIG_AUTH_TOKEN_ACCESS_TOKEN_LIFE_SECONDS}") accessTokenLifeSeconds: String,
        @Value("\${$CONFIG_AUTH_TOKEN_REFRESH_TOKEN_LIFE_SECONDS}") refreshTokenLifeSeconds: String
    ) = AuthenticationPolicy(
        validationCacheCount = validationCacheCount.toIntOrNull() ?: throw IllegalConfigValueException(
            CONFIG_AUTH_TOKEN_VALIDATION_CACHE_COUNT,
            validationCacheCount,
            Int::class
        ),
        issuer = issuerName,
        audience = audience,
        accessTokenLifeInSeconds = accessTokenLifeSeconds.toLongOrNull() ?: throw IllegalConfigValueException(
            CONFIG_AUTH_TOKEN_ACCESS_TOKEN_LIFE_SECONDS,
            accessTokenLifeSeconds,
            Long::class
        ),
        refreshTokenLifeInSeconds = refreshTokenLifeSeconds.toLongOrNull() ?: throw IllegalConfigValueException(
            CONFIG_AUTH_TOKEN_REFRESH_TOKEN_LIFE_SECONDS,
            refreshTokenLifeSeconds,
            Long::class
        )
    )

    companion object {
        const val BYPASS_AUTH_URI_LIST = "SecurityConfig.securityBypassUris"

        private const val CONFIG_AUTH_TOKEN = "sandboxapp.authentication.token"
        private const val CONFIG_AUTH_TOKEN_ISSUER = "$CONFIG_AUTH_TOKEN.issuer"
        private const val CONFIG_AUTH_TOKEN_AUDIENCE = "$CONFIG_AUTH_TOKEN.audience"
        private const val CONFIG_AUTH_TOKEN_VALIDATION_CACHE_COUNT = "$CONFIG_AUTH_TOKEN.validationCacheCount"
        private const val CONFIG_AUTH_TOKEN_ACCESS_TOKEN_LIFE_SECONDS = "$CONFIG_AUTH_TOKEN.accessTokenLifeSeconds"
        private const val CONFIG_AUTH_TOKEN_REFRESH_TOKEN_LIFE_SECONDS = "$CONFIG_AUTH_TOKEN.refreshTokenLifeSeconds"
    }
}
