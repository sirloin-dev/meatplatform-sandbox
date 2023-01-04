/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.medium.jpa.repository.auth

import net.meatplatform.sandbox.domain.auth.repository.ProviderAuthRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import testcase.medium.MediumTestBase

/**
 * @since 2022-02-14
 */
@ContextConfiguration(classes = [ProviderAuthRepositorySpec::class])
class ProviderAuthRepositorySpec : MediumTestBase() {
    @Autowired
    private lateinit var sut: ProviderAuthRepository

    @Test
    fun placeholder() {
        TODO("Implement this")
    }
}
