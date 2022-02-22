/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.model

/**
 * Model object 에 optimistic locking 을 구현할 때 활용할 수 있습니다.
 *
 * @since 2022-02-14
 */
interface Versioned<T : Comparable<T>> {
    val version: T

    companion object {
        const val DEFAULT_INT = 1
        const val DEFAULT_LONG_INT = 1L
    }
}
