/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.large

import org.slf4j.Logger
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.jdbc.core.JdbcTemplate
import java.io.FileNotFoundException
import java.sql.Statement
import javax.sql.DataSource

/**
 * `@DataJdbcTest` 가 제공하는 H2Database 및 Test repository 생성 과정을 Customise 할 방법을 찾지 못했습니다.
 * 이 때문에 Production 에서는 문제없는 sql 문을 올바르게 실행하지 못하는 문제를 해결하기 위해
 * DataSource 와 JdbcTemplate 을 재정의하고, 테스트 실행에 필요한 sql 문을 실행합니다.
 *
 * `@DataJdbcTest` + `@EnableAutoConfiguration` 조합으로 Test db 생성에 성공한다면 이 로직을 삭제해도 됩니다.
 *
 * 기존의 Bean 을 override 하기 때문에, 아래의 Spring 설정을 추가로 해 줘야 합니다.
 *
 * ```
 * spring.main.allowBeanDefinitionOverriding=true
 * ```
 *
 * @since 2022-02-14
 */
@TestConfiguration
class SpringDataJdbcTestConfig(
    private val env: Environment,
    private val log: Logger
) {
    @Bean
    fun jdbcTemplate(): JdbcTemplate {
        val dataSource = testDataSource()

        with(env) {
            // DDL Query 실행
            forEachSqlFiles(dataSource, getProperty("${PREFIX_SQL_INIT}.schema-locations")) { statement, sql ->
                statement.execute(sql)
            }

            // DML Query 실행
            forEachSqlFiles(dataSource, getProperty("${PREFIX_SQL_INIT}.data-locations")) { statement, sql ->
                statement.executeUpdate(sql)
            }
        }

        return JdbcTemplate(dataSource)
    }

    // 반드시 runtime classpath 에 h2 의존성을 선언해야 합니다.
    private fun testDataSource(): DataSource = with(env) {
        return@with DataSourceBuilder.create()
            .driverClassName(getProperty("${PREFIX_DATA_SOURCE}.driver-class-name"))
            .url(getProperty("${PREFIX_DATA_SOURCE}.url"))
            .username(getProperty("${PREFIX_DATA_SOURCE}.username"))
            .password(getProperty("${PREFIX_DATA_SOURCE}.password"))
            .build()
    }

    private fun <T> forEachSqlFiles(
        ds: DataSource,
        resourcePath: String?,
        onEachSqlStatement: (Statement, String) -> T
    ) {
        if (resourcePath.isNullOrEmpty()) {
            return
        }

        val stmt = ds.connection.createStatement()

        // Statement 는 kotlin 의 .use 를 쓸 수 없다
        @Suppress("ConvertTryFinallyToUseCall")
        try {
            val resolver = PathMatchingResourcePatternResolver()
            val sqlFiles: Array<Resource> = try {
                resolver.getResources(resourcePath)
            } catch (e: FileNotFoundException) {
                log.warn("SQL File '{}' 이 없습니다.", resourcePath)
                return
            }

            sqlFiles.forEach { sqlFile ->
                sqlFile.extractSqlCommands().forEach forEachSqlCommand@{ sql ->
                    val trimmed = sql.trim()
                    if (trimmed.startsWith("--") || trimmed.startsWith("/*")) {
                        return@forEachSqlCommand
                    }
                    onEachSqlStatement(stmt, trimmed)
                }
            }
        } finally {
            stmt.close()
        }
    }

    private fun Resource.extractSqlCommands(): List<String> =
        file.readText().replace("\n", "").split(";")

    companion object {
        private const val PREFIX_DATA_SOURCE = "spring.datasource"
        private const val PREFIX_SQL_INIT = "spring.sql.init"
    }
}
