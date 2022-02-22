/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.large.domain.user

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import test.small.domain.randomUser

class SaveAndFindUserTest : UserRepositoryLargeTestBase() {
    @DisplayName("저장한 Entity model 을 검색할 수 있다")
    @Test
    fun `Should find by query after entity model is saved`() {
        // given:
        val user = randomUser()

        // when:
        val savedUser = sut.save(user)

        // then:
        val foundUser = sut.findByUuid(savedUser.uuid)

        // expect:
        assertThat(foundUser, `is`(savedUser))
    }
}
