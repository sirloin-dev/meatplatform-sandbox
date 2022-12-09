/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.medium.domain.repository.user

import net.meatplatform.sandbox.domain.repository.auth.ProviderAuthRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import testcase.medium.MediumTestBase

/**
 * @since 2022-02-14
 */
@ContextConfiguration(classes = [UserRepositoryImplSpec::class])
class UserRepositoryImplSpec : MediumTestBase() {
    @Autowired
    private lateinit var sut: ProviderAuthRepository

    @DisplayName("저장한 User 를 ID 로 다시 조회할 수 있다.")
    @Test
    fun aaa() {
        // given:
        println(sut)
    }
}
