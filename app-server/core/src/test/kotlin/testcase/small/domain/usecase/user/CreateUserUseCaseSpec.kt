/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.small.domain.usecase.user

import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.repository.auth.ProviderAuthRepository
import net.meatplatform.sandbox.domain.repository.user.UserRepository
import net.meatplatform.sandbox.domain.usecase.user.CreateUserUseCase
import net.meatplatform.sandbox.domain.usecase.user.CreateUserUseCaseImpl
import net.meatplatform.sandbox.exception.external.user.UserWithProviderIdentityAlreadyExist
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import test.com.sirloin.annotation.SmallTest
import test.domain.usecase.auth.random
import test.domain.usecase.user.CreateUserUseCaseMessageImpl
import test.domain.usecase.user.expectCreatedUser

/**
 * @since 2022-02-14
 */
@SmallTest
class CreateUserUseCaseSpec {
    private lateinit var providerAuths: ProviderAuthRepository
    private lateinit var users: UserRepository
    private lateinit var sut: CreateUserUseCase

    @BeforeEach
    fun setup() {
        providerAuths = mock()
        users = mock()
        sut = CreateUserUseCaseImpl(providerAuths, users)

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
        val result = sut.createUser(message)

        // expect:
        expectCreatedUser(result, isReflecting = message)
    }

    @DisplayName("이미 등록한 Provider Authentication 으로 또 이용자를 등록할 수 없다.")
    @Test
    fun userWithDuplicateEmailNotAllowed() {
        // given:
        val message = CreateUserUseCaseMessageImpl.random()

        // and:
        sut.createUser(message)

        // when:
        `when`(providerAuths.findByIdentity(any(), any())).thenReturn(
            ProviderAuthentication.random(type = message.authenticationType, password = message.password)
        )

        // then:
        assertThrows<UserWithProviderIdentityAlreadyExist> {
            sut.createUser(message)
        }
    }
}
