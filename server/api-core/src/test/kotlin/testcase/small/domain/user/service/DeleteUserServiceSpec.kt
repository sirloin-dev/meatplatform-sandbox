/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.small.domain.user.service

import com.sirloin.sandbox.server.core.domain.user.exception.UserNotFoundException
import com.sirloin.sandbox.server.core.domain.user.repository.UserRepository
import com.sirloin.sandbox.server.core.domain.user.service.DeleteUserService
import com.sirloin.sandbox.server.core.i18n.LocaleProvider
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import test.com.sirloin.annotation.SmallTest
import test.small.domain.randomUser
import test.small.domain.user.MockUserRepository
import java.util.*

@SmallTest
class DeleteUserServiceSpec {
    private lateinit var sut: DeleteUserService
    private lateinit var userRepo: UserRepository

    @BeforeEach
    fun setUp() {
        this.userRepo = MockUserRepository()
        this.sut = DeleteUserService.newInstance(userRepo, LocaleProvider.defaultInstance())
    }

    @DisplayName("없는 이용자를 삭제하려 하면 UserNotFoundException 이 발생한다")
    @Test
    fun `Throws UserNotFoundException for nonexistent user`() {
        assertThrows<UserNotFoundException> {
            sut.deleteUserByUuid(UUID.randomUUID())
        }
    }

    @DisplayName("생성한 이용자를 삭제하면, isDeleted 상태가 된다")
    @Test
    fun `User status is changed to 'isDeleted'`() {
        // given:
        val savedUser = userRepo.save(randomUser())

        // then:
        val deletedUser = sut.deleteUserByUuid(savedUser.uuid)

        // expect:
        assertThat(deletedUser.isDeleted, `is`(true))
    }
}
