/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1

import net.meatplatform.sandbox.advice.responseDecorator.V1ResponseDecorator

/**
 * [V1ResponseDecorator] 는 이 Annotation 을 붙인 클래스들을 JSON 형태로 직렬화합니다.
 *
 * 클라이언트 응답으로 내려줄 클래스는 가급적 불변 `data class` 으로 선언해 주시기 바랍니다.
 *
 * @since 2022-02-14
 * @see V1ResponseDecorator
 */
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.PROPERTY_GETTER,
)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ResponseV1
