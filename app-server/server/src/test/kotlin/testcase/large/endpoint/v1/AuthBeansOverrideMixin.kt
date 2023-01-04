/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.large.endpoint.v1

import net.meatplatform.sandbox.domain.auth.repository.ProviderAuthRepository
import org.springframework.context.ApplicationContext
import test.domain.repository.auth.SpyProviderAuthRepository

/**
 * @since 2022-02-14
 */
interface AuthBeansOverrideMixin {
    val applicationContext: ApplicationContext

    /**
     * ApiLargeTestConfig 에서 [ProviderAuthRepository.NAME] Bean 설정을 SpyProviderAuthRepository 로
     * override 했다고 가정하고 작성합니다.
     */
    fun getSpyProviderAuthRepository(): SpyProviderAuthRepository {
        val spyProviderAuthRepository = applicationContext.getBean(ProviderAuthRepository.NAME)

        return spyProviderAuthRepository as SpyProviderAuthRepository
    }
}
