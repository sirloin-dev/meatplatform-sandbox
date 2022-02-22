/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.small.domain.user.service

import com.github.javafaker.Faker
import com.sirloin.sandbox.server.core.domain.user.User
import com.sirloin.sandbox.server.core.domain.user.exception.UserNotFoundException
import com.sirloin.sandbox.server.core.domain.user.repository.UserRepository
import com.sirloin.sandbox.server.core.domain.user.service.UpdateUserService
import com.sirloin.sandbox.server.core.i18n.LocaleProvider
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import test.com.sirloin.annotation.SmallTest
import test.small.domain.randomUser
import test.small.domain.user.MockUserRepository
import java.util.*

@Suppress("ClassName")  // Test fixture 이름 잘 짓기 위해 무시
@SmallTest
class UpdateUserServiceSpec {
    private lateinit var sut: UpdateUserService
    private lateinit var userRepo: UserRepository

    @BeforeEach
    fun setUp() {
        this.userRepo = MockUserRepository()
        this.sut = UpdateUserService.newInstance(userRepo, LocaleProvider.defaultInstance())
    }

    @DisplayName("입력한 값을 이용해 이용자 정보를 변경한다")
    @Test
    fun `User information is updated with given values`() {
        // given:
        val savedUser = userRepo.save(randomUser())
        val (newName, newProfileImageUrl) = with(Faker()) {
            name().username() to internet().image()
        }

        // then:
        val updatedUser = sut.updateUser(
            uuid = savedUser.uuid,
            nickname = newName,
            profileImageUrl = newProfileImageUrl
        )

        // expect:
        assertAll(
            { assertThat(updatedUser.nickname, `is`(newName)) },
            { assertThat(updatedUser.profileImageUrl, `is`(newProfileImageUrl)) }
        )
    }

    @DisplayName("생략한 필드는 업데이트 되지 않는다")
    @Nested
    inner class `Omitted field is not updated` {
        private lateinit var savedUser: User
        private lateinit var newName: String
        private lateinit var newProfileImageUrl: String

        @BeforeEach
        fun setUp() {
            savedUser = userRepo.save(randomUser())
            with(Faker()) {
                newName = name().username()
                newProfileImageUrl = internet().image()
            }
        }

        @DisplayName("nickname 을 생략하면 변경되지 않는다")
        @Test
        fun `nickname does not change if omitted`() {
            // then:
            val updatedUser = sut.updateUser(
                uuid = savedUser.uuid,
                profileImageUrl = newProfileImageUrl
            )

            // expect:
            assertAll(
                { assertThat(updatedUser.nickname, `is`(savedUser.nickname)) },
                { assertThat(updatedUser.profileImageUrl, `is`(newProfileImageUrl)) }
            )
        }

        @DisplayName("profileImageUrl 을 생략하면 변경되지 않는다")
        @Test
        fun `profileImageUrl does not change if omitted`() {
            // then:
            val updatedUser = sut.updateUser(
                uuid = savedUser.uuid,
                nickname = newName,
            )

            // expect:
            assertAll(
                { assertThat(updatedUser.nickname, `is`(newName)) },
                { assertThat(updatedUser.profileImageUrl, `is`(savedUser.profileImageUrl)) }
            )
        }
    }

    @DisplayName("없는 이용자의 정보를 업데이트 하려 하면 UserNotFoundException 이 발생한다")
    @Test
    fun `Error when updating nonexistent user`() {
        // expect:
        assertThrows<UserNotFoundException> {
            sut.updateUser(UUID.randomUUID())
        }
    }
}
