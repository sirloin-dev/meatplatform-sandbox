/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.auth.repository

import com.sirloin.jvmlib.time.truncateToSeconds
import net.meatplatform.sandbox.domain.auth.RsaCertificate
import java.time.Instant
import java.util.*

/**
 * @since 2022-12-26
 */
interface RsaCertificateRepository {
    fun findById(id: UUID): RsaCertificate?

    fun findCurrentlyActive(): RsaCertificate?

    fun issueRandom(issuedAt: Instant = Instant.now().truncateToSeconds()): RsaCertificate

    fun save(certificate: RsaCertificate): RsaCertificate

    companion object {
        const val NAME = "net.meatplatform.sandbox.domain.auth.RsaCertificateRepository"
    }
}
