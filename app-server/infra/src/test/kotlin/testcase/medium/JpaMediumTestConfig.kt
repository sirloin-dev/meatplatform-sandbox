/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.medium

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * @since 2022-02-14
 */
@TestConfiguration
@EntityScan(JpaMediumTestConfig.JPA_PACKAGE_NAME)
@EnableJpaRepositories(JpaMediumTestConfig.JPA_PACKAGE_NAME)
internal class JpaMediumTestConfig {
    companion object {
        const val JPA_PACKAGE_NAME = "net.meatplatform.sandbox.jpa.*"
    }
}
