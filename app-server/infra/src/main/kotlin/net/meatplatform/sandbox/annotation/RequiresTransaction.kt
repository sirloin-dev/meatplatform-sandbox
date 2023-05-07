/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.annotation

/**
 * 이 Annotation 이 붙어있는 함수 또는 클래스를 실행하기 위해서는 Transaction 이 필요함을 나타냅니다.
 *
 * @since 2022-04-11
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class RequiresTransaction
