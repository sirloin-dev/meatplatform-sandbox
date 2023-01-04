/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test

import com.github.javafaker.Faker

/**
 * @since 2023-02-14
 */
object SharedTestObjects {
    /**
     * Faker 객체 만들 때 마다 내부의 yml 을 읽어들이기 때문에 테스트 반복 실행시 성능 저하 문제가 있다.
     * 따라서 Test cycle 동안 단일한 Faker 인스턴스를 재활용하기 위해 구현한다.
     *
     * 다만 Thread safety 여부는 확인하지 못했다.
     */
    val faker: Faker by lazy { Faker() }
}
