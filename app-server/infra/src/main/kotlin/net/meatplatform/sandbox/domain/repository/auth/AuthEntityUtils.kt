/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.repository.auth

import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication
import net.meatplatform.sandbox.jpa.entity.auth.UserAuthenticationEntity
import net.meatplatform.sandbox.jpa.entity.user.UserEntity

internal fun UserAuthenticationEntity.importValues(
    newValue: ProviderAuthentication,
    owner: UserEntity? = null
): UserAuthenticationEntity {
    this.user = owner
    this.type = newValue.type
    this.providerId = newValue.providerId
    this.password = newValue.password
    this.name = newValue.name

    return this
}

internal fun ProviderAuthentication.toEntity(owner: UserEntity?): UserAuthenticationEntity =
    UserAuthenticationEntity().importValues(this, owner)

internal fun ProviderAuthentication.Companion.fromEntity(
    authEntity: UserAuthenticationEntity
): ProviderAuthentication = with(authEntity) {
    create(
        type = type,
        providerId = providerId,
        password = password,
        name = name
    )
}
