/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain

/**
 * @since 2022-02-14
 */
interface IdHolder<T> {
    val id: T

    val isIdentifiable: Boolean
}
