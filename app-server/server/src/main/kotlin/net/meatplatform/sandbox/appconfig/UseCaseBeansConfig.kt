/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.appconfig

import net.meatplatform.sandbox.annotation.UseCase
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.Configuration
import org.springframework.core.type.filter.AnnotationTypeFilter

/**
 * @since 2022-12-09
 */
@Configuration
class UseCaseBeansConfig : BeanDefinitionRegistryPostProcessor {
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
    }

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter(AnnotationTypeFilter(UseCase::class.java))

        // Speedup for specific package names
        scanner.findCandidateComponents(USE_CASE_PACKAGE_NAME)
            .forEach {
                // According to the documentation using this as bean name here could be dangerous,
                // but using this value is safe unless annotated class is a simple(a final type) object.
                val name = it.beanClassName!!

                registry.registerBeanDefinition(name, it)
            }
    }

    companion object {
        const val USE_CASE_PACKAGE_NAME = "net.meatplatform.sandbox.domain.usecase"
    }
}
