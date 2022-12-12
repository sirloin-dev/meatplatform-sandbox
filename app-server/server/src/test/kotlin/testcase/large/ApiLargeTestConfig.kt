/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.large

import net.meatplatform.sandbox.domain.repository.auth.ProviderAuthRepository
import org.junit.jupiter.api.Order
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.Ordered
import test.domain.repository.auth.SpyProviderAuthRepository

/**
 * Large Test 들에 활용할 Spring Test Application 환경 설정 모음
 *
 * 본래 `spring.main.allow-bean-definition-overriding=true` 로 간편하게 할 수 있는 설정들이었으나
 * Spring boot 2.7.1 업그레이드 이후 yml 설정이 동작하지 않는 현상을 발견해 직접 bean override 수행
 *
 * Type 기반 override 가 되지 않기 때문에, Qualifier name 기반으로 bean override 설정을 다시 해 줘야 한다.
 *
 * @since 2022-02-14
 */
@Order(Ordered.LOWEST_PRECEDENCE)
@TestConfiguration
class ApiLargeTestConfig(
    private val applicationContext: GenericApplicationContext
) {
    @Bean
    fun spyProviderAuthRepository(
        @Qualifier(ProviderAuthRepository.NAME)
        delegate: ProviderAuthRepository
    ): ProviderAuthRepository {
        applicationContext.registerBean(
            ProviderAuthRepository.NAME,
            SpyProviderAuthRepository::class.java,
            delegate
        )

        return applicationContext.getBean(ProviderAuthRepository.NAME) as ProviderAuthRepository
    }
}
