/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.model

import java.time.Instant

/**
 * Model object 의 생성 시기와 변경 이력을 추적할 때 활용할 수 있다.
 *
 * @since 2022-02-14
 */
interface DateAuditable {
    val createdAt: Instant

    val updatedAt: Instant
}
