/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.audit

/**
 * @since 2022-02-14
 */
interface Identifiable<T> {
    val id: T

    val isIdentifiable: Boolean
}
