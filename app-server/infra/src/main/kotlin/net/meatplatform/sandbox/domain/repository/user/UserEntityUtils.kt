/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.repository.user

import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.model.user.User
import net.meatplatform.sandbox.domain.repository.auth.fromEntity
import net.meatplatform.sandbox.domain.repository.auth.toEntity
import net.meatplatform.sandbox.jpa.entity.user.UserEntity

internal fun UserEntity.importValues(newValue: User): UserEntity {
    this.id = newValue.id
    this.nickname = newValue.nickname
    this.profileImageUrl = newValue.profileImageUrl
    this.softDelete.createdAt = newValue.createdAt
    this.softDelete.updatedAt = newValue.updatedAt
    this.linkedAuthenticationEntities = newValue.authentications.map { it.toEntity(this) }.toMutableSet()

    return this
}

internal fun User.toEntity(): UserEntity = UserEntity().importValues(this)

internal fun User.Companion.fromEntity(userEntity: UserEntity): User = with(userEntity) {
    create(
        id = id,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        authentications = linkedAuthenticationEntities.map { ProviderAuthentication.fromEntity(it) },
        createdAt = softDelete.createdAt,
        updatedAt = softDelete.updatedAt
    )
}
