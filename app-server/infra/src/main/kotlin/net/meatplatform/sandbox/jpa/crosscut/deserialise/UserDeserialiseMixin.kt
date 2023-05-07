/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.crosscut.deserialise

import net.meatplatform.sandbox.domain.user.User
import net.meatplatform.sandbox.jpa.entity.user.UserEntity

/**
 * @since 2023-05-07
 */
internal interface UserDeserialiseMixin : AuthenticationDeserialiseMixin {
    fun UserEntity.toUser(): User =
        User.create(
            id = id,
            nickname = nickname,
            profileImageUrl = profileImageUrl,
            authentications = linkedAuthenticationEntities.map { it.toProviderAuthentication() },
            createdAt = softDelete.createdAt,
            updatedAt = softDelete.updatedAt
        )
}
