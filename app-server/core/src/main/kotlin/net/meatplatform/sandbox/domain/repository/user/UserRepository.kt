/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.repository.user

import net.meatplatform.sandbox.domain.model.user.User
import java.util.*

/**
 * @since 2022-02-14
 */
interface UserRepository {
    fun findById(id: UUID): User?

    /** 인자로 전달받은 User 를 무조건 Repository 내에 생성: INSERT operation. */
    fun create(user: User): User

    /** 인자로 전달받은 User 를 저장 또는 수정: UPSERT operation. */
    fun save(user: User): User
}
