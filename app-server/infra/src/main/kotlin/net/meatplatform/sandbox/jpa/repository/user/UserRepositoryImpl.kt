/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.repository.user

import net.meatplatform.sandbox.annotation.InfrastructureService
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.user.User
import net.meatplatform.sandbox.domain.user.repository.UserRepository
import net.meatplatform.sandbox.jpa.dao.read.UserAuthenticationEntityReadDao
import net.meatplatform.sandbox.jpa.dao.read.UserEntityReadDao
import net.meatplatform.sandbox.jpa.dao.write.UserEntityWriteDao
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * @since 2022-02-14
 */
@InfrastructureService(UserRepository.NAME)
internal class UserRepositoryImpl(
    private val userReader: UserEntityReadDao,
    private val userWriter: UserEntityWriteDao,
    private val userAuthReader: UserAuthenticationEntityReadDao
) : UserRepository {
    @Transactional
    override fun findById(id: UUID): User? = userReader.findById(id)?.let { User.fromEntity(it) }

    @Transactional
    override fun findByProviderAuth(providerAuth: ProviderAuthentication): User? {
        val maybeUserAuthEntity = providerAuth.run {
            when (type) {
                ProviderAuthentication.Type.EMAIL_AND_PASSWORD,
                ProviderAuthentication.Type.GOOGLE,
                ProviderAuthentication.Type.APPLE ->
                    userAuthReader.findByIdentity(type, providerId, password)

                else -> throw IllegalArgumentException("$type is not a provider authentication.")
            }
        }

        return maybeUserAuthEntity?.user?.let { User.fromEntity(it) }
    }

    @Transactional
    override fun save(user: User): User {
        val userEntity = user.run {
            if (isIdentifiable) {
                userReader.findById(id)?.importValues(this) ?: toEntity()
            } else {
                toEntity()
            }
        }

        val savedEntity = userWriter.save(userEntity)
        return User.fromEntity(savedEntity)
    }
}
