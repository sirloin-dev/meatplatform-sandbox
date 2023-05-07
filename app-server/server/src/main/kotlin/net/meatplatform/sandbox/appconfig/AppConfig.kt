/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.appconfig

import net.meatplatform.sandbox.CoreApplication
import net.meatplatform.sandbox.SandboxApplication
import net.meatplatform.sandbox.SandboxApplication.Companion.BuildConfig
import net.meatplatform.sandbox.domain.auth.RsaCertificate
import net.meatplatform.sandbox.exception.internal.IllegalConfigValueException
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Application 전체에 활용할 환경 설정 처리 로직
 *
 * @since 2022-02-14
 */
@Configuration
internal class AppConfig(
    private val log: Logger,
) {
    @Bean
    fun buildConfig(): BuildConfig = SandboxApplication.buildConfig.also {
        this.log.info("Build configurations - ")
        this.log.info("  Version:     {}", it.version)
        this.log.info("  Fingerprint: {}", it.fingerprint)
        this.log.info("  Profile:     {}", it.profile)
    }

    @Bean
    fun rsaCertificatePolicy(
        @Value("\${$CONFIG_AUTH_RSA_CERT_CACHE_CAPACITY}") cacheCapacity: String,
        @Value("\${$CONFIG_AUTH_RSA_CERT_KEY_SIZE}") keySize: String,
        @Value("\${$CONFIG_AUTH_RSA_CERT_KEY_ACTIVE_SECONDS}") keyActiveSeconds: String
    ): RsaCertificate.Policy = RsaCertificate.Policy.create(
        cacheCapacity = cacheCapacity.toIntOrNull()
            ?: throw IllegalConfigValueException(CONFIG_AUTH_RSA_CERT_CACHE_CAPACITY, cacheCapacity, Int::class),
        keySize = keySize.toIntOrNull()
            ?: throw IllegalConfigValueException(CONFIG_AUTH_RSA_CERT_KEY_SIZE, cacheCapacity, Int::class),
        activeSeconds = keyActiveSeconds.toLongOrNull()
            ?: throw IllegalConfigValueException(CONFIG_AUTH_RSA_CERT_KEY_ACTIVE_SECONDS, cacheCapacity, Long::class)
    )

    companion object {
        private const val CONFIG_AUTH_RSA_CERT = "sandboxapp.authentication.rsaCert"
        private const val CONFIG_AUTH_RSA_CERT_CACHE_CAPACITY = "$CONFIG_AUTH_RSA_CERT.cacheCapacity"
        private const val CONFIG_AUTH_RSA_CERT_KEY_SIZE = "$CONFIG_AUTH_RSA_CERT.keySize"
        private const val CONFIG_AUTH_RSA_CERT_KEY_ACTIVE_SECONDS = "$CONFIG_AUTH_RSA_CERT.keyActiveSeconds"
    }
}
