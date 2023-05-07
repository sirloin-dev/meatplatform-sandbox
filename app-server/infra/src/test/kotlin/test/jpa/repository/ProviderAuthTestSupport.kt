/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.jpa.repository

import com.sirloin.jvmlib.util.EMPTY_UUID
import net.meatplatform.sandbox.annotation.RequiresTransaction
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.user.User
import net.meatplatform.sandbox.domain.user.repository.UserRepository
import net.meatplatform.sandbox.jpa.crosscut.deserialise.AuthenticationDeserialiseMixin
import net.meatplatform.sandbox.jpa.crosscut.serialise.AuthenticationSerialiseMixin
import net.meatplatform.sandbox.jpa.crosscut.serialise.UserSerialiseMixin
import net.meatplatform.sandbox.jpa.dao.write.RsaCertificateEntityWriteDao
import test.domain.usecase.user.random
import testcase.medium.MediumTestBase

/**
 * @since 2023-05-07
 */
object ProviderAuthTestSupport : UserSerialiseMixin, AuthenticationSerialiseMixin, AuthenticationDeserialiseMixin {
    @RequiresTransaction
    fun MediumTestBase.saveProviderAuth(providerAuth: ProviderAuthentication): ProviderAuthentication {
        val randomUser = User.random(
            id = EMPTY_UUID,
            authentications = listOf(providerAuth)
        )
        val userRepository = applicationContext.getBean(UserRepository.NAME) as UserRepository

        val savedUser = userRepository.save(randomUser)

        return savedUser.authentications.first { it == providerAuth }
    }
}
