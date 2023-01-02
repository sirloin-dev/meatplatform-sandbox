/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.dao.write

import net.meatplatform.sandbox.jpa.entity.auth.RsaCertificateEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * @since 2022-12-29
 */
internal interface RsaCertificateEntityWriteDao {
    fun save(entity: RsaCertificateEntity): RsaCertificateEntity
}

@Repository
internal interface WriteRsaCertificateEntityJpaRepository : JpaRepository<RsaCertificateEntity, UUID>

@Repository
internal class RsaCertificateEntityWriteDaoImpl(
    private val delegate: WriteRsaCertificateEntityJpaRepository
) : RsaCertificateEntityWriteDao {
    override fun save(entity: RsaCertificateEntity): RsaCertificateEntity =
        delegate.save(entity)
}
