/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.medium.domain.repository.user

import io.kotest.matchers.shouldBe
import net.meatplatform.sandbox.domain.model.user.User
import net.meatplatform.sandbox.domain.repository.user.UserRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import test.domain.usecase.user.random
import testcase.medium.MediumTestBase
import java.util.*

/**
 * @since 2022-02-14
 */
@ContextConfiguration(classes = [UserRepositorySpec::class])
class UserRepositorySpec : MediumTestBase() {
    @Autowired
    private lateinit var sut: UserRepository

    @DisplayName("없는 User 를 조회하면 null 을 반환한다.")
    @Test
    fun nullReturnedForNotExistingUser() {
        // then:
        val foundUser = sut.findById(UUID.randomUUID())

        // expect:
        foundUser shouldBe null
    }

    @DisplayName("생성한 User 를 ID 로 다시 조회할 수 있다.")
    @Test
    fun createdUserCouldBeFound() {
        // given:
        val user = User.random()

        // when:
        val savedUser = sut.save(user)

        // then:
        val foundUser = sut.findById(savedUser.id)

        // expect:
        foundUser shouldBe savedUser
    }

    @DisplayName("수정한 User 를 ID 로 다시 조회할 수 있다.")
    @Test
    fun savedUserCouldBeFound() {
        // given:
        val user = User.random()

        // when:
        val savedUser = sut.save(user)

        // then:
        val foundUser = sut.findById(savedUser.id)

        // expect:
        foundUser shouldBe savedUser
    }
}
