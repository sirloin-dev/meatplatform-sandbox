/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.medium

import com.sirloin.sandbox.server.core.CoreApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import test.com.sirloin.annotation.LargeTest

/**
 * Repository 관련 로직을 테스트할 때, 번거로운 환경설정을 상속으로 해결할 수 있도록 하는 Template Class
 * 코드 공유를 위한 상속이므로 좋은 패턴은 아님
 *
 * @since 2022-02-14
 */
@LargeTest
@DataJdbcTest
@EnableAutoConfiguration
@EnableJdbcRepositories(CoreApplication.PACKAGE_NAME)
class SpringDataJdbcMediumTestBase
