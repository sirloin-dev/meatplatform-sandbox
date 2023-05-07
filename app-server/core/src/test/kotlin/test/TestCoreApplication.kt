/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test

import net.meatplatform.sandbox.CoreApplication
import net.meatplatform.sandbox.appconfig.ClientVersionConfigHolder
import net.meatplatform.sandbox.appconfig.OAuthConfig
import net.meatplatform.sandbox.util.ToStringHelper
import test.util.TestToStringHelper

/**
 * @since 2023-05-06
 */
class TestCoreApplication : CoreApplication {
    override fun toStringHelper(): ToStringHelper = SharedTestObjects.toStringHelper

    override fun clientVersionConfig(): ClientVersionConfigHolder {
        TODO("Not yet implemented")
    }

    override fun oAuthConfig(): OAuthConfig {
        TODO("Not yet implemented")
    }
}
