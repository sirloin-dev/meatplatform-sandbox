/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.small

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import test.com.sirloin.annotation.SmallTest
import java.security.Security

/**
 * @since 2022-02-14
 */
@SmallTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SmallTestBase {
    @BeforeAll
    fun installBCSecurityProvider() {
        Security.addProvider(BouncyCastleProvider())
    }

    @AfterAll
    fun uninstallBCSecurityProvider() {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
    }
}
