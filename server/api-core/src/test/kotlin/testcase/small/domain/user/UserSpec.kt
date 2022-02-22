/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.small.domain.user

import com.sirloin.jvmlib.time.truncateToSeconds
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import test.com.sirloin.annotation.SmallTest
import test.small.domain.randomUser
import java.time.Instant

@SmallTest
class UserSpec {
    @DisplayName("생성한 User 를 삭제하면 삭제 시점의 시간이 기록된다")
    @Test
    fun `Timestamp is saved when deleting a user`() {
        // given:
        val user = randomUser()

        // when:
        val beforeDelete = Instant.now().truncateToSeconds()
        val deletedUser = user.edit().delete()

        // then:
        assertAll(
            { assertThat(deletedUser.isDeleted, `is`(true)) },
            { assertThat(deletedUser.deletedAt, greaterThanOrEqualTo(beforeDelete)) }
        )
    }
}
