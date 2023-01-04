/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.medium

import net.meatplatform.sandbox.domain.auth.RsaCertificate
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import java.security.Security
import org.bouncycastle.jce.provider.BouncyCastleProvider

/**
 * @since 2022-02-14
 */
@TestConfiguration
@EntityScan(JpaMediumTestConfig.JPA_PACKAGE_NAME)
@EnableJpaRepositories(JpaMediumTestConfig.JPA_PACKAGE_NAME)
internal class JpaMediumTestConfig {
    init {
        Security.addProvider(BouncyCastleProvider())
    }

    @Bean
    fun rsaCertificatePolicy(): RsaCertificate.Policy = RsaCertificate.Policy.create()

    companion object {
        const val JPA_PACKAGE_NAME = "net.meatplatform.sandbox.jpa.*"
    }
}
