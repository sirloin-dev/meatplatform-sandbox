/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test

import net.meatplatform.sandbox.CoreApplication
import net.meatplatform.sandbox.util.ToStringHelper
import test.util.TestToStringHelper

/**
 * @since 2023-05-06
 */
class TestCoreApplication : CoreApplication {
    private val toStringHelper = TestToStringHelper()

    override fun toStringHelper(): ToStringHelper = toStringHelper
}
