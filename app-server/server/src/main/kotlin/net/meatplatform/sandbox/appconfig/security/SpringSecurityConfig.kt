/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.appconfig.security

import com.fasterxml.jackson.databind.ObjectMapper
import net.meatplatform.sandbox.domain.auth.repository.RsaCertificateRepository
import net.meatplatform.sandbox.exception.internal.IllegalConfigValueException
import net.meatplatform.sandbox.security.AuthorizationHeaderFilter
import net.meatplatform.sandbox.security.CorsFilter
import net.meatplatform.sandbox.security.EnforcedSecurityCheckRequestMatcher
import net.meatplatform.sandbox.security.authentication.AuthenticationTokenVerifier
import net.meatplatform.sandbox.security.authentication.AuthenticationTokenVerifierImpl
import net.meatplatform.sandbox.security.authentication.HttpAuthenticationProvider
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.util.matcher.RequestMatcher

/**
 * @since 2022-03-20
 */
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
class SpringSecurityConfig(
    @Autowired(required = false)
    @Qualifier(BYPASS_AUTH_URI_LIST)
    private val securityBypassRequestMatchers: List<RequestMatcher>,
) {
    @Bean
    fun authenticationTokenVerifier(
        policy: AuthenticationPolicy,
        objectMapper: ObjectMapper,
        rsaCerts: RsaCertificateRepository,
    ): AuthenticationTokenVerifier = AuthenticationTokenVerifierImpl(
        policy,
        objectMapper,
        rsaCerts,
        LoggerFactory.getLogger(AuthenticationTokenVerifier::class.java)
    )

    @Bean
    fun filterChain(
        http: HttpSecurity,
        authenticationTokenVerifier: AuthenticationTokenVerifier
    ): SecurityFilterChain {
        http
            .addFilterBefore(CorsFilter(), ChannelProcessingFilter::class.java)
            .cors().disable()   // CORS 정책 직접 핸들링
            .csrf().disable()
            .anonymous().disable()  // 일단 모든 Endpoint 를 secure 하고 bypass 전략을 직접 구현하도록 한다.
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
            .requestMatchers(EnforcedSecurityCheckRequestMatcher(securityBypassRequestMatchers)).permitAll()
            .and()
            .authenticationProvider(HttpAuthenticationProvider(authenticationTokenVerifier))
            .addFilterBefore(AuthorizationHeaderFilter(), BasicAuthenticationFilter::class.java)
            .authorizeHttpRequests()
            .anyRequest()
            .authenticated()

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
