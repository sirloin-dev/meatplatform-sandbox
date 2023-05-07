/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.small.domain.usecase.auth

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.auth.repository.ProviderAuthRepository
import net.meatplatform.sandbox.domain.auth.usecase.LoginUseCase
import net.meatplatform.sandbox.domain.user.User
import net.meatplatform.sandbox.domain.user.repository.UserRepository
import net.meatplatform.sandbox.exception.external.auth.ProviderAuthenticationFailedException
import net.meatplatform.sandbox.exception.external.user.UserByProviderAuthenticationNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import test.SharedTestObjects.faker
import test.com.sirloin.util.random.randomEnum
import test.domain.usecase.auth.random
import test.domain.usecase.user.random
import test.util.randomAlphanumeric
import testcase.small.SmallTestBase

/**
 * @since 2023-01-02
 */
class LoginUseCaseSpec : SmallTestBase() {
    private lateinit var sut: LoginUseCase

    private lateinit var providerAuthRepository: ProviderAuthRepository
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setup() {
        providerAuthRepository = mock()
        userRepository = mock()

        sut = LoginUseCase.newInstance(
            providerAuthRepository,
            userRepository
        )
    }

    @DisplayName("Email Login 에 해당하는 Provider Auth 가 없다면 UserByProviderAuthenticationNotFoundException 이 발생한다")
    @Test
    fun errorIfNoEmailPAIsFound() {
        // given:
        val message = randomEmailLoginMessage()

        // when:
        `when`(providerAuthRepository.findByEmailAuthIdentity(any(), any())).thenReturn(null)

        // then:
        shouldThrow<UserByProviderAuthenticationNotFoundException> { login(message) }
    }

    @DisplayName("제3자 인증정보 검증 실패시 ProviderAuthenticationFailedException 이 발생한다")
    @Test
    fun errorIfThirdPartyAuthVerificationFailed() {
        // given:
        val message = randomThirdPartyAuthLoginMessage()

        // when:
        `when`(providerAuthRepository.verifyProviderAuth(any(), any())).thenThrow(
            ProviderAuthenticationFailedException(message.type, message.authToken)
        )

        // then:
        shouldThrow<ProviderAuthenticationFailedException> { login(message) }
    }

    @DisplayName("Email Login 에 해당하는 Provider Auth 를 찾았을 때:")
    @Nested
    inner class WhenProviderAuthForEmailIsFound {
        private lateinit var message: LoginUseCase.EmailLoginMessage

        @BeforeEach
        fun setup() {
            message = randomEmailLoginMessage()

            `when`(providerAuthRepository.findByEmailAuthIdentity(any(), any())).thenReturn(
                ProviderAuthentication.random(type = ProviderAuthentication.Type.EMAIL_AND_PASSWORD)
            )
        }

        @DisplayName("User 의 가입 이력이 없다면 UserByProviderAuthenticationNotFoundException 이 발생한다.")
        @Test
        fun errorIfNoUserIsFound() {
            // expect:
            shouldThrow<UserByProviderAuthenticationNotFoundException> { login(message) }
        }

        @DisplayName("User 의 가입 이력이 있다면 User 를 반환한다.")
        @Test
        fun returnsUserIfFound() {
            // given:
            val expectedUser = User.random()

            // when:
            `when`(userRepository.findByProviderAuth(any())).thenReturn(expectedUser)

            // then:
            val user = login(message)

            // expect:
            user shouldBe expectedUser
        }
    }

    @DisplayName("제3자 인증정보 검증 성공시:")
    @Nested
    inner class WhenThirdPartyAuthVerificationWasSuccessful {
        private lateinit var message: LoginUseCase.ThirdPartyAuthLoginMessage

        @BeforeEach
        fun setup() {
            message = randomThirdPartyAuthLoginMessage()

            `when`(providerAuthRepository.verifyProviderAuth(any(), any())).thenReturn(
                ProviderAuthentication.random(type = message.type)
            )
        }

        @DisplayName("User 의 가입 이력이 없다면 UserByProviderAuthenticationNotFoundException 이 발생한다.")
        @Test
        fun errorIfNoUserIsFound() {
            // expect:
            shouldThrow<UserByProviderAuthenticationNotFoundException> { login(message) }
        }

        @DisplayName("User 의 가입 이력이 있다면 User 를 반환한다.")
        @Test
        fun returnsUserIfFound() {
            // given:
            val expectedUser = User.random()

            // when:
            `when`(userRepository.findByProviderAuth(any())).thenReturn(expectedUser)

            // then:
            val user = login(message)

            // expect:
            user shouldBe expectedUser
        }
    }

    private fun randomEmailLoginMessage(
        email: String = faker.internet().emailAddress(),
        password: String = randomAlphanumeric(8, 32)
    ): LoginUseCase.EmailLoginMessage = LoginUseCase.EmailLoginMessage(
        email = email,
        password = password
    )

    private fun randomThirdPartyAuthLoginMessage(
        type: ProviderAuthentication.Type = randomEnum(ProviderAuthentication.Type::class) { it.isThirdPartyAuth },
        authToken: String = randomAlphanumeric(128, 128)
    ): LoginUseCase.ThirdPartyAuthLoginMessage = LoginUseCase.ThirdPartyAuthLoginMessage(
        type = type,
        authToken = authToken
    )

    private fun login(
        message: LoginUseCase.Message,
        ipAddressStr: String = "localhost"
    ): User = sut.getUserByProviderAuthentication(message, ipAddressStr)
}
