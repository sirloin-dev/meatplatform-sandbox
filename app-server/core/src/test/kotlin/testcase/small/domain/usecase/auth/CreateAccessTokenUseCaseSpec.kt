/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.small.domain.usecase.auth

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.auth.RsaCertificate
import net.meatplatform.sandbox.domain.user.User
import net.meatplatform.sandbox.domain.auth.repository.RsaCertificateRepository
import net.meatplatform.sandbox.domain.auth.usecase.CreateAccessTokenUseCase
import net.meatplatform.sandbox.exception.internal.IpAuthenticationNotFoundException
import org.junit.jupiter.api.*
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import test.domain.usecase.auth.random
import test.domain.usecase.user.random
import testcase.small.SmallTestBase
import java.time.Instant

/**
 * @since 2022-12-26
 */
class CreateAccessTokenUseCaseSpec : SmallTestBase() {
    private lateinit var rsaCerts: RsaCertificateRepository
    private lateinit var sut: CreateAccessTokenUseCase

    @BeforeEach
    fun setup() {
        val issuerName = "net.meatplatform.sandbox"
        val accessTokenLifeSeconds = 3 * 60L
        val refreshTokenLifeSeconds = 30 * 60L
        this.rsaCerts = mock()

        this.sut = CreateAccessTokenUseCase.newInstance(
            issuerName,
            accessTokenLifeSeconds,
            refreshTokenLifeSeconds,
            rsaCerts
        )

        `when`(rsaCerts.issueRandom(any())).thenAnswer { RsaCertificate.random(issuedAt = it.arguments[0] as Instant) }
        `when`(rsaCerts.save(any())).thenAnswer { it.arguments[0] }
    }

    @DisplayName("User 가 IP 인증정보를:")
    @Nested
    inner class WhileIpAuthenticationIs {
        @DisplayName("가지고 있지 않으면 토큰을 만들 수 없다")
        @Test
        fun notIncludedCannotCreateAccessToken() {
            // given:
            val user = User.random(authentications = emptySet())

            // expect:
            shouldThrow<IpAuthenticationNotFoundException> { sut.createTokenOf(user) }
        }

        @DisplayName("가지고 있어야만 토큰을 만들 수 있다")
        @Test
        fun includedCanCreateAccessToken() {
            // given:
            val user = User.random(authentications = setOf(
                ProviderAuthentication.random(type = ProviderAuthentication.Type.IP_ADDRESS)
            ))

            // then:
            shouldNotThrowAny { sut.createTokenOf(user) }
        }
    }
}
