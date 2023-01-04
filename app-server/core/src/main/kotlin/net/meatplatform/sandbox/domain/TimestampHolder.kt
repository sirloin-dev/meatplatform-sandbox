/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain

import java.time.Instant

/**
 * @since 2022-02-14
 */
interface TimestampHolder {
    val createdAt: Instant

    val updatedAt: Instant

    companion object {
        val EMPTY_INSTANT: Instant = Instant.MIN

        fun create(
            createdAt: Instant = EMPTY_INSTANT,
            updatedAt: Instant = EMPTY_INSTANT
        ): TimestampHolder {
            val now = Instant.now()

            return TimestampHolderMutator(
                createdAt = createdAt.takeIf { it != EMPTY_INSTANT } ?: now,
                updatedAt = updatedAt.takeIf { it != EMPTY_INSTANT } ?: now
            )
        }
    }
}

data class TimestampHolderMutator(
    override val createdAt: Instant,
    override val updatedAt: Instant
) : TimestampHolder {
    companion object {
        fun from(src: TimestampHolder): TimestampHolderMutator = with(src) {
            if (this is TimestampHolderMutator) {
                this
            } else {
                TimestampHolderMutator(
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )
            }
        }
    }
}
