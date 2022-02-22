/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.large.domain.user

import com.sirloin.sandbox.server.core.domain.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import testcase.large.SpringDataJdbcLargeTestBase

/**
 * UserRepository 테스트 로직에 반복되는 부분을 추출한 Template Class
 *
 * @since 2022-02-14
 */
@ContextConfiguration(classes = [UserRepository::class])
class UserRepositoryLargeTestBase : SpringDataJdbcLargeTestBase() {
    @Autowired
    private lateinit var _userRepository: UserRepository

    protected val sut: UserRepository
        get() = _userRepository
}
