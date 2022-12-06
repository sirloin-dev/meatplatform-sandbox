/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.annotation

/**
 * Use case 로직임을 나타내는 어노테이션.
 * Annotation processor 등을 이용해 후처리에 사용할 수 있습니다.
 *
 * @since 2022-05-26
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class UseCase
