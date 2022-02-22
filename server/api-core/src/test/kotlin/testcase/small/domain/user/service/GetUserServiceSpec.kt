/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.small.domain.user.service

import com.sirloin.sandbox.server.core.domain.user.exception.UserNotFoundException
import com.sirloin.sandbox.server.core.domain.user.repository.UserRepository
import com.sirloin.sandbox.server.core.domain.user.service.GetUserService
import com.sirloin.sandbox.server.core.i18n.LocaleProvider
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import test.com.sirloin.annotation.SmallTest
import test.small.domain.randomUser
import test.small.domain.user.MockUserRepository
import java.util.*

@SmallTest
class GetUserServiceSpec {
    private lateinit var sut: GetUserService
    private lateinit var userRepo: UserRepository

    @BeforeEach
    fun setUp() {
        this.userRepo = MockUserRepository()
        this.sut = GetUserService.newInstance(userRepo, LocaleProvider.defaultInstance())
    }

    @DisplayName("생성하고 저장한 이용자를 찾을 수 있다")
    @Test
    fun `Can find created and saved user`() {
        // given:
        val savedUser = userRepo.save(randomUser())

        // then:
        val gotUser = sut.getUserByUuid(savedUser.uuid)
        val foundUser = sut.findUserByUuid(savedUser.uuid)

        // expect:
        assertAll(
            { assertThat(gotUser, `is`(savedUser)) },
            { assertThat(foundUser, `is`(savedUser)) },
            { assertThat(foundUser, `is`(gotUser)) }
        )
    }

    @DisplayName("없는 이용자를 find 하려고 하면 null 을 반환한다")
    @Test
    fun `Returns null for nonexistent user`() {
        // when:
        val nonExistentUser = sut.findUserByUuid(UUID.randomUUID())

        // expect:
        assertThat(nonExistentUser, `is`(nullValue()))
    }

    @DisplayName("없는 이용자를 get 하려고 하면 UserNotFoundException 이 발생한다")
    @Test
    fun `Throws UserNotFoundException for nonexistent user`() {
        assertThrows<UserNotFoundException> {
            sut.getUserByUuid(UUID.randomUUID())
        }
    }
}
