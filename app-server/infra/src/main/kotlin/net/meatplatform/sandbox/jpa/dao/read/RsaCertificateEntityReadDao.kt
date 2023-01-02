/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.dao.read

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import net.meatplatform.sandbox.jpa.entity.auth.RsaCertificateEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

/**
 * @since 2022-12-29
 */
internal interface RsaCertificateEntityReadDao {
    fun findById(id: UUID): RsaCertificateEntity?

    fun findLatestActiveUntil(timestamp: Instant): RsaCertificateEntity?
}

@Repository
internal interface ReadRsaCertificateEntityJpaRepository : JpaRepository<RsaCertificateEntity, UUID> {
    @Query("""
        SELECT r
        FROM RsaCertificateEntity r
        WHERE r.isEnabled = true
          AND r.id = ?1
    """)
    fun findByCertificateId(certificateId: UUID): RsaCertificateEntity?
}

@Repository
internal class RsaCertificateEntityReadDaoImpl(
    private val delegate: ReadRsaCertificateEntityJpaRepository
) : RsaCertificateEntityReadDao {
    @PersistenceContext
    private lateinit var em: EntityManager

    override fun findById(id: UUID): RsaCertificateEntity? =
        delegate.findByCertificateId(id)

    override fun findLatestActiveUntil(timestamp: Instant): RsaCertificateEntity? = em.createQuery(
        """
            SELECT r
            FROM RsaCertificateEntity r
            WHERE r.isEnabled = true
              AND r.activeUntil >= :timestamp
            ORDER BY r.issuedAt DESC
        """.trimIndent(), RsaCertificateEntity::class.java
    ).setParameter("timestamp", timestamp)
        .setMaxResults(1)
        .resultList
        .takeIf { it.isNotEmpty() }
        ?.first()
}
