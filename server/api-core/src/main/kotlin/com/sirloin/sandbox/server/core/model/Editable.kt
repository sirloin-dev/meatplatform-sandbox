/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.model

/**
 * Model object 를 편집할 필요가 있을 때 활용할 타입.
 *
 * @since 2022-02-14
 */
interface Editable<T> {
    fun edit(): T
}
