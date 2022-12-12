/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.annotation

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

/**
 * Spring 의 [Service] 어노테이션은 뜻이 너무 광범위하기 때문에, 의미를 좀더 명확하게 하고자 새로운 타입을 정의했습니다.
 *
 * @since 2022-12-09
 */
@Service
annotation class InfrastructureService(
    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     * @return the suggested component name, if any (or empty String otherwise)
     */
    @get:AliasFor(annotation = Component::class)
    val value: String = ""
)
