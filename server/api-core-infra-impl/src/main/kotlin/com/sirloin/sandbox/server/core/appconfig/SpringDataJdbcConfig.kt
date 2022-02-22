/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core.appconfig

import com.sirloin.sandbox.server.core.CoreApplication
import com.sirloin.sandbox.server.core.converter.BytesUuidConverters
import com.sirloin.sandbox.server.core.converter.InstantStringConverters
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories

/**
 * Spring data-jdbc 환경 설정입니다.
 *
 * @since 2022-02-14
 */
@Configuration
@EnableJdbcRepositories(CoreApplication.PACKAGE_NAME)
class SpringDataJdbcConfig : AbstractJdbcConfiguration() {
    override fun userConverters(): List<Converter<*, *>> {
        return listOf(
            BytesUuidConverters.READ_CONVERTER, BytesUuidConverters.WRITE_CONVERTER,
            InstantStringConverters.READ_CONVERTER, InstantStringConverters.WRITE_CONVERTER
        )
    }
}
