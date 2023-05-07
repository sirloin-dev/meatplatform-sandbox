/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.medium.jpa.repository.auth

import io.kotest.matchers.shouldBe
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.auth.repository.ProviderAuthRepository
import net.meatplatform.sandbox.util.PasswordCodecMixin
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import test.SharedTestObjects.faker
import test.com.sirloin.util.random.randomEnum
import test.domain.usecase.auth.random
import test.jpa.repository.ProviderAuthTestSupport.saveProviderAuth
import test.util.randomAlphanumeric
import testcase.medium.MediumTestBase

/**
 * @since 2022-02-14
 */
@ContextConfiguration(classes = [ProviderAuthRepositorySpec::class])
class ProviderAuthRepositorySpec : MediumTestBase(), PasswordCodecMixin {
    @Autowired
    private lateinit var sut: ProviderAuthRepository

    @DisplayName("저장하지 않은:")
    @Nested
    inner class CannotFindProviderAuthenticationWhen {
        @DisplayName("Email Provider Authentication 을 찾을 수 없다.")
        @Test
        fun cannotFindNotExistEmailProviderAuthentication() {
            // then:
            val result = sut.findByEmailAuthIdentity(faker.internet().emailAddress(), randomAlphanumeric())

            // expect:
            result shouldBe null
        }

        @DisplayName("Third Party Provider Authentication 을 찾을 수 없다.")
        @Test
        fun cannotFindNotExistThirdPartyProviderAuthentication() {
            // then:
            val result = sut.findByProviderAuthIdentity(
                randomEnum(ProviderAuthentication.Type::class) { it.isThirdPartyAuth }, randomAlphanumeric()
            )

            // expect:
            result shouldBe null
        }
    }

    @DisplayName("저장한 Email Authentication 을 찾을 수 있다.")
    @Test
    fun findSavedEmailProviderAuthentication() {
        // given:
        val savedEmailAuth = saveProviderAuth(
            ProviderAuthentication.random(ProviderAuthentication.Type.EMAIL_AND_PASSWORD)
        )

        // then:
        val result = savedEmailAuth.run {
            sut.findByEmailAuthIdentity(providerId, password!!)
        }

        // expect:
        result shouldBe savedEmailAuth
    }

    @DisplayName("저장한 Third Party Authentication 을 찾을 수 있다.")
    @Test
    fun findSavedThirdPartyProviderAuthentication() {
        // given:
        val savedProviderAuth = saveProviderAuth(
            ProviderAuthentication.random(randomEnum(ProviderAuthentication.Type::class) { it.isThirdPartyAuth })
        )

        // then:
        val result = savedProviderAuth.run {
            sut.findByProviderAuthIdentity(type, providerId)
        }

        // expect:
        result shouldBe savedProviderAuth
    }
}
