/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.crosscut.serialise

import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.auth.RsaCertificate
import net.meatplatform.sandbox.jpa.entity.auth.RsaCertificateEntity
import net.meatplatform.sandbox.jpa.entity.auth.UserAuthenticationEntity
import net.meatplatform.sandbox.jpa.entity.user.UserEntity

/**
 * @since 2023-05-07
 */
internal interface AuthenticationSerialiseMixin {
    fun ProviderAuthentication.toUserAuthenticationEntity(
        ownerUserEntity: UserEntity
    ) : UserAuthenticationEntity =
        UserAuthenticationEntity.create(this, ownerUserEntity)

    fun RsaCertificate.toRsaCertificateEntity() : RsaCertificateEntity =
        RsaCertificateEntity.create(this)
}
