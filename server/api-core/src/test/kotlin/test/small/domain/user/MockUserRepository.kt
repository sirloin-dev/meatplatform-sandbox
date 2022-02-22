/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package test.small.domain.user

import com.sirloin.sandbox.server.core.domain.user.User
import com.sirloin.sandbox.server.core.domain.user.repository.UserRepository
import java.util.*

/**
 * Small/Medium Test 단계에서 필요시 활용할 UserRepository 의 메모리 구현체
 *
 * @since 2022-02-14
 */
class MockUserRepository : UserRepository {
    private val store = HashMap<UUID, User>()

    override fun save(user: User): User {
        val model = User.from(user)
        store[user.uuid] = model

        return model
    }

    override fun findByUuid(uuid: UUID): User? =
        store[uuid]?.let { User.from(it) }
}
