/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain

import com.sirloin.jvmlib.util.EMPTY_UUID
import java.util.*

/**
 * @since 2022-12-09
 */
interface IdHolderUUID : IdHolder<UUID> {
    override val id: UUID

    override val isIdentifiable: Boolean
        get() = id != EMPTY_UUID
}
