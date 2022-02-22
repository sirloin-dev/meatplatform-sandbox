/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.large.domain.user

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import test.small.domain.randomUser

class DeleteAndFindUserTest : UserRepositoryLargeTestBase() {
    @DisplayName("삭제한 Entity 는 더 이상 조회할 수 없다")
    @Test
    fun `Cannot query already deleted entity`() {
        // given:
        val savedUser = sut.save(randomUser())

        // when:
        sut.save(savedUser.edit().delete())

        // then:
        val nonExistentUser = sut.findByUuid(savedUser.uuid)

        // expect:
        assertThat(nonExistentUser, `is`(nullValue()))
    }
}
