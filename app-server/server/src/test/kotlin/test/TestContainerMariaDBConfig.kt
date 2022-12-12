/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test

import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import com.sirloin.jvmlib.net.pathSegments
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.testcontainers.containers.MariaDBContainer
import java.net.URI

/**
 * TestContainers + JUnit Jupiter 를 이용한 자동 lifecycle 관리는 편리하지만
 * 매 Test suite 마다 test container 가 up -> down 을 반복하므로 all test 실행시 속도가 매우 느려지는 문제가 있다.
 *
 * 원칙상으로는 그게 맞지만 실제로는 업무 효율이 매우 떨어지기 때문에, TestContainer 의 생명주기를 수동으로 직접 관리한다.
 *
 * 참고: [Manual container lifecycle control](https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control/#singleton-containers)
 *
 * @since 2022-06-20
 */
@TestConfiguration
@Order(value = Ordered.HIGHEST_PRECEDENCE)
class TestContainerMariaDBConfig {
    @PostConstruct
    fun onApplicationStart() = synchronized(CONTAINER_LOCK) {
        runningContainer?.let {
            if (!it.isRunning) {
                it.start()
            }
        }
    }

    @PreDestroy
    fun onApplicationDestroy() = synchronized(CONTAINER_LOCK) {
        runningContainer?.let {
            if (it.isRunning) {
                it.stop()
            }
        }
    }

    companion object {
        private var runningContainer: MariaDBContainer<*>? = null

        private val CONTAINER_LOCK = Any()
        private const val MARIADB_IMAGE_NAME = "mariadb:10.10.2"

        @Suppress("SameParameterValue")     // Template 화 가능하므로 argument 받을 수 있도록 구현함
        private fun newMariaDBContainer(
            hostPort: Int,
            databaseName: String,
            username: String,
            password: String
        ): MariaDBContainer<*> = MariaDBContainer(MARIADB_IMAGE_NAME)
            .withCreateContainerCmdModifier {
                it.withHostConfig(
                    HostConfig().withPortBindings(
                        PortBinding(Ports.Binding.bindPort(hostPort), ExposedPort(3306))
                    )
                )
            }
            .withReuse(true)
            .withDatabaseName(databaseName)
            .withUsername(username)
            .withPassword(password)

        fun forceStartContainer(config: Map<String, *>): Unit = synchronized(CONTAINER_LOCK) {
            // jdbc:mariadb://host:port/database?params
            val jdbcUri = URI.create((config["url"] as String).substring("jdbc:".length))

            if (runningContainer == null) {
                runningContainer = newMariaDBContainer(
                    hostPort = jdbcUri.port,
                    databaseName = jdbcUri.pathSegments().first(),
                    username = config["username"] as String,
                    password = config["password"] as String
                )
            }

            runningContainer?.let {
                if (!it.isRunning) {
                    it.start()
                }
            }
        }
    }
}
