/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.large

import com.sirloin.sandbox.server.core.CoreApplication
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.AbstractTestExecutionListener
import test.com.sirloin.annotation.LargeTest

/**
 * Repository 관련 로직을 테스트할 때, 번거로운 환경설정을 상속으로 해결할 수 있도록 하는 Template Class
 * 코드 공유를 위한 상속이므로 좋은 패턴은 아님
 *
 * @since 2022-02-14
 */
@LargeTest
@DataJdbcTest
@ComponentScan(CoreApplication.PACKAGE_NAME)
@ContextConfiguration(classes = [SpringDataJdbcTestConfig::class])
@TestExecutionListeners(
    value = [SpringDataJdbcLargeTestBase::class],
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
class SpringDataJdbcLargeTestBase : AbstractTestExecutionListener() {
    override fun beforeTestClass(testContext: TestContext) {
    }

    override fun prepareTestInstance(testContext: TestContext) {
    }

    override fun beforeTestExecution(testContext: TestContext) {
    }

    override fun beforeTestMethod(testContext: TestContext) {
    }
}
