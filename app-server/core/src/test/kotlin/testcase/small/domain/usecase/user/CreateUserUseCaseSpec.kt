/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.small.domain.usecase.user

import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.user.User
import net.meatplatform.sandbox.domain.auth.repository.ProviderAuthRepository
import net.meatplatform.sandbox.domain.user.repository.UserRepository
import net.meatplatform.sandbox.domain.user.usecase.CreateUserUseCase
import net.meatplatform.sandbox.exception.external.user.UserWithProviderIdentityAlreadyExist
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import test.domain.usecase.auth.random
import test.domain.usecase.user.CreateUserUseCaseMessageImpl
import test.domain.usecase.user.expectCreatedUser
import test.domain.usecase.user.random
import testcase.small.SmallTestBase

/**
 * @since 2022-02-14
 */
class CreateUserUseCaseSpec : SmallTestBase() {
    private lateinit var providerAuths: ProviderAuthRepository
    private lateinit var users: UserRepository
    private lateinit var sut: CreateUserUseCase

    @BeforeEach
    fun setup() {
        providerAuths = mock()
        users = mock()
        sut = CreateUserUseCase.newInstance(providerAuths, users)

        // 수정필요
        `when`(providerAuths.verifyProviderAuth(any(), any())).thenAnswer {
            ProviderAuthentication.random(type = it.arguments[0] as ProviderAuthentication.Type)
        }
        `when`(users.save(any())).thenAnswer { it.arguments[0] }
    }

    @DisplayName("Email 과 비밀번호를 이용해 이용자를 생성할 수 있다.")
    @Test
    fun userWithEmailAndPasswordCreated() {
        // given:
        val message = CreateUserUseCaseMessageImpl.random()

        // then:
        val result = createUser(message)

        // expect:
        expectCreatedUser(result, isReflecting = message)
    }

    @DisplayName("이미 등록한 Provider Authentication 으로 또 이용자를 등록할 수 없다.")
    @Test
    fun userWithDuplicateEmailNotAllowed() {
        // given:
        val message = CreateUserUseCaseMessageImpl.random()

        // and:
        createUser(message)

        // when:
        `when`(users.findByProviderAuth(any())).thenReturn(User.random())

        // then:
        assertThrows<UserWithProviderIdentityAlreadyExist> {
            createUser(message)
        }
    }

    private fun createUser(
        message: CreateUserUseCase.Message,
        ipAddressStr: String = "localhost"
    ): User {
        return sut.createUser(message, ipAddressStr)
    }
}
