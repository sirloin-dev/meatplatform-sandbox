/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.util

import com.sirloin.jvmlib.util.EMPTY_UUID
import java.util.*
import kotlin.reflect.KClass

/**
 * @since 2023-04-15
 */
object JpaEntityUtils {
    fun <T : Any, U : Any> Collection<T>.assertNotEmptyOf(sourceObjectType: KClass<out U>) {
        if (this.isEmpty()) {
            throw IllegalArgumentException("${sourceObjectType.simpleName} transformation source objects are empty")
        }
    }

    /**
     * 숫자 id 에 0 이하의 값을 쓰는 것은 정보의 이식성이 낮아 매우 좋지 않다.
     * 따라서 0 이하의 숫자를 id 로 지정한 record 를 발견한 경우 spring-data-jpa 의 구현에 맞춰 `false` 를 반환한다.
     *
     * 왜냐면 spring-data-jpa 는 id 가 Number 타입일 때 0/0L 인 경우에만 new 여부를 올바르게 검사하기 때문이다.
     *
     * 자세한 내용은
     *
     * - [org.springframework.data.jpa.repository.support.SimpleJpaRepository.save]
     * - [org.springframework.data.repository.core.EntityInformation.isNew]
     * - [org.springframework.data.repository.core.support.AbstractEntityInformation.isNew]
     *
     * 구현을 살펴보세요.
     */
    fun Number?.isValidJpaNumberId(): Boolean = when (this) {
        is Int -> this > 0
        is Long -> this > 0
        null -> false
        else -> throw UnsupportedOperationException("Unable to determine number id correctness for ${this::class}")
    }

    fun UUID?.isValidJpaUUID(): Boolean = this != null && this != EMPTY_UUID
}
