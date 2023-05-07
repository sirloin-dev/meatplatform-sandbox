/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.crosscut.serialise

import net.meatplatform.sandbox.domain.user.User
import net.meatplatform.sandbox.jpa.entity.user.UserEntity

/**
 * @since 2023-05-07
 */
internal interface UserSerialiseMixin {
    fun User.toUserEntity(): UserEntity = UserEntity.create(this)
}
