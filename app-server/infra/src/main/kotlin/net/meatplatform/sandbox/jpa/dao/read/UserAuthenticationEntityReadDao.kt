/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.dao.read

import com.sirloin.jvmlib.text.isNullOrUnicodeBlank
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.jpa.entity.auth.UserAuthenticationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * @since 2023-01-04
 */
internal interface UserAuthenticationEntityReadDao {
    fun findByIdentity(
        type: ProviderAuthentication.Type,
        providerId: String,
        encodedPassword: String? = null
    ): UserAuthenticationEntity?
}

@Repository
internal interface ReadUserAuthenticationEntityJpaRepository : JpaRepository<UserAuthenticationEntity, Long> {
    @Query("""
        SELECT ua
        FROM UserAuthenticationEntity ua
        WHERE ua.type = ?1
          AND ua.providerId = ?2
    """)
    fun findByProviderAuthIdentity(
        type: ProviderAuthentication.Type,
        providerId: String
    ): UserAuthenticationEntity?

    @Query("""
        SELECT ua
        FROM UserAuthenticationEntity ua
        WHERE ua.type = ?1
          AND ua.providerId = ?2
          AND ua.password = ?3
    """)
    fun findByEmailAuthIdentity(
        type: ProviderAuthentication.Type,
        providerId: String,
        encodedPassword: String
    ): UserAuthenticationEntity?
}

@Repository
internal class UserAuthenticationEntityReadDaoImpl(
    private val delegate: ReadUserAuthenticationEntityJpaRepository
) : UserAuthenticationEntityReadDao {
    override fun findByIdentity(
        type: ProviderAuthentication.Type,
        providerId: String,
        encodedPassword: String?
    ): UserAuthenticationEntity? = if (type == ProviderAuthentication.Type.EMAIL_AND_PASSWORD) {
        if (encodedPassword.isNullOrUnicodeBlank()) {
            throw IllegalArgumentException("Empty or blank password is provided for EMAIL_AND_PASSWORD login.")
        } else {
            delegate.findByEmailAuthIdentity(type, providerId, requireNotNull(encodedPassword))
        }
    } else {
        delegate.findByProviderAuthIdentity(type, providerId)
    }
}
