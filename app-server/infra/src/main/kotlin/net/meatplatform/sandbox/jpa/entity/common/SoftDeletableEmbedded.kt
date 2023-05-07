/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.entity.common

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import net.meatplatform.sandbox.annotation.RequiresTransaction
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant
import java.util.*

/**
 * @since 2022-12-09
 */
@Embeddable
class SoftDeletableEmbedded(time: Instant = Instant.now()) {
    @get:Column(name = "is_deleted")
    var isDeleted: Boolean = false

    @get:CreatedDate
    @get:Column(name = "created_at")
    var createdAt: Instant = time

    @get:LastModifiedDate
    @get:Column(name = "updated_at")
    var updatedAt: Instant = time

    @RequiresTransaction
    fun importValues(other: SoftDeletableEmbedded): SoftDeletableEmbedded {
        this.isDeleted = other.isDeleted
        this.createdAt = other.createdAt
        this.updatedAt = other.updatedAt

        return this
    }

    override fun equals(other: Any?): Boolean = if (other !is SoftDeletableEmbedded) {
        false
    } else {
        this.isDeleted == other.isDeleted &&
                this.createdAt == other.createdAt &&
                this.updatedAt == other.updatedAt
    }

    override fun hashCode(): Int = Objects.hash(
        isDeleted,
        createdAt,
        updatedAt
    )
}
