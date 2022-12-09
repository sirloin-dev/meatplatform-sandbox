/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.audit.indentifiable

import com.sirloin.jvmlib.util.EMPTY_UUID
import net.meatplatform.sandbox.audit.Identifiable
import java.util.*

/**
 * @since 2022-12-09
 */
interface UUIDIdentifiable : Identifiable<UUID> {
    override val id: UUID

    override val isIdentifiable: Boolean
        get() = id != EMPTY_UUID
}
