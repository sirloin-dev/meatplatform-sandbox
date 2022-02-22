/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.large.domain.user

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import test.small.domain.randomUser

class UpdateAndFindUserTest : UserRepositoryLargeTestBase() {
    @DisplayName("Entity 를 수정한 내용은 그대로 반영된다")
    @Test
    fun `Updated information is stored and retrieved intact`() {
        // given:
        val savedUser = sut.save(randomUser())
        val originalName = savedUser.nickname

        // when:
        val updatedUser = sut.save(savedUser.edit().apply {
            nickname = "__TESTER"
        })

        // then:
        val foundUser = sut.findByUuid(updatedUser.uuid)

        // expect:
        assertAll(
            { assertThat(foundUser, `is`(updatedUser)) },
            { assertThat(foundUser!!.nickname, not(originalName)) }
        )
    }
}
