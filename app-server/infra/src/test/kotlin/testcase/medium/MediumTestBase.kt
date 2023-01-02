/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.medium

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration
import test.com.sirloin.annotation.MediumTest

/**
 * Repository 관련 로직을 테스트할 때, 번거로운 환경설정을 상속으로 해결할 수 있도록 하는 Template Class
 * 코드 공유를 위한 상속이므로 좋은 패턴은 아님
 *
 * @since 2022-02-14
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)    // 환경설정에서 지정한 DB 에서 테스트 실행
@ContextConfiguration(
    classes = [
        JpaMediumTestConfig::class,
    ],
)
@ComponentScan(
    basePackages = ["net.meatplatform.sandbox"]
)
@MediumTest
class MediumTestBase {
    @Autowired
    private lateinit var _applicationContext: ApplicationContext

    protected val applicationContext: ApplicationContext
        get() = _applicationContext
}
