/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa

import net.meatplatform.sandbox.jpa.util.ImportEntityValuesGuard

/**
 * Value import 를 편리하게 하기 위한 JPA Entity helper. Type [T] 는 이 클래스를 상속하는 자기 자신이 되어야 합니다.
 * 제약 사항을 어길 경우, value import 시점에 [ClassCastException] 이 발생할 수 있습니다.
 *
 * e.g.)
 * ```
 * class SomeJpaEntity(
 *   override val id: UUID
 * ) : ValueImporterJpaEntityTemplate<UUID, SomeJpaEntity>()
 * ```
 *
 * @since 2023-05-07
 */
abstract class ValueImporterJpaEntityTemplate<ID : Any, T> :
    ImportEntityValuesGuard<ValueImporterJpaEntityTemplate<ID, T>> {
    abstract val id: ID

    @Suppress("UNCHECKED_CAST")     // 주석의 지시사항을 지켰다고 간주한다면 T와 U 는 같은 타입이다.
    fun <U : ValueImporterJpaEntityTemplate<ID, T>> importValues(newValue: U): U {
        return importIfAllowed(newValue, ValueImporterJpaEntityTemplate<ID, T>::id) {
            importValuesInternal(newValue as T)

            return@importIfAllowed this
        } as U
    }

    protected fun <U_ID, U_TYPE, U : ValueImporterJpaEntityTemplate<U_ID, U_TYPE>> MutableCollection<U>.mergeMany(
        rightValues: Collection<U>
    ) {
        val newEntities = rightValues.associateBy { it.id }.toMutableMap()
        val removals = HashSet<U>()

        forEach {
            if (newEntities.containsKey(it.id)) {
                val rhs = (newEntities[it.id] as U)
                it.importValues(rhs)
                newEntities.remove(it.id)
            } else {
                removals.add(it)
            }
        }

        removeAll(removals)
        addAll(newEntities.values)
    }

    protected abstract fun importValuesInternal(newValue: T)
}
