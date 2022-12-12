/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.annotation

/**
 * kotlin-allopen 컴파일러 플러그인 사용시, 이 마크를 붙인 클래스는 디자인 상으로는 non-open 이지만
 * 테스트를 위한 proxy, mock 생성을 위해 컴파일 시점에 open 으로 바꿔주라는 지시자 어노테이션입니다.
 *
 * 자세한 내용은 [kotlin-allopen](https://kotlinlang.org/docs/all-open-plugin.html) 컴파일러 플러그인 문서를
 * 참고하세요.
 *
 * @since 2022-02-14
 */
@Target(AnnotationTarget.CLASS)
annotation class OpenForTesting
