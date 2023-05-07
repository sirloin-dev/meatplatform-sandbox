/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.util

import net.meatplatform.sandbox.jpa.util.JpaEntityUtils.isValidJpaNumberId
import net.meatplatform.sandbox.jpa.util.JpaEntityUtils.isValidJpaUUID
import java.util.*

/**
 * Entity value import 시에, import message 를 자신의 타입으로 표현한 경우가 종종 있다.
 * 그런 경우 Entity 의 정의에 맞도록 동일한 값의 다른 객체를 생성하지 못하도록 막는다.
 *
 * import value message 를 자신의 타입으로 표현하지 않는 경우에는 이 mix-in 을 이용하지 않아도 된다.
 *
 * 참고자료: [Martin Fowler blog - Eric Evans' Classification](https://martinfowler.com/bliki/EvansClassification.html)
 *
 * Glossary:
 * - lhs: Left-hand side of equal sign
 * - rhs: Right-hand side of equal sign
 *
 * @since 2023-04-15
 */
interface ImportEntityValuesGuard<T : Any> {
    // Left 를 조작할 Right 의 메시지 타입이 Left 와 다른 경우는 import 가능여부 알아서 구현해 주시기 바랍니다.
    fun <ID : Any> T.importIfAllowed(
        rightValues: T?,
        idProvider: (T) -> ID?,
        onImportAllowed: T.(rightValues: T) -> T
    ): T {
        if (rightValues == null) {
            return this
        }

        val leftId = idProvider(this)
        val rightId = idProvider(rightValues)

        if (rightId != null) {
            when (rightId) {
                is UUID -> assertValuesImportable(leftId as? UUID, rightId) { it.isValidJpaUUID() }
                is Int -> assertValuesImportable(leftId as? Int, rightId) { it.isValidJpaNumberId() }
                is Long -> assertValuesImportable(leftId as? Long, rightId) { it.isValidJpaNumberId() }
                is String -> assertValuesImportable(leftId as? String, rightId) { !it.isNullOrBlank() }

                // 그 외 id type 들은 필요한 경우 직접 구현
                else -> throw IllegalArgumentException(
                    "ImportValuesGuard does not support (${rightId::class.simpleName})."
                )
            }
        }

        return this.onImportAllowed(rightValues)
    }

    private fun <T> assertValuesImportable(left: T?, right: T?, leftIdValidator: (T?) -> Boolean) {
        if (leftIdValidator(left) && left != right) {
            throw IllegalArgumentException(
                "Cannot import values of right object(id = ${right}) into left object(id = ${left})."
            )
        }
    }
}
