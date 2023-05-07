/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test

import net.meatplatform.sandbox.CoreApplication
import net.meatplatform.sandbox.appconfig.ClientVersionConfigHolder
import net.meatplatform.sandbox.appconfig.OAuthConfig
import net.meatplatform.sandbox.util.ToStringHelper

/**
 * @since 2023-05-07
 */
class InfraTestCoreApplicationImpl : CoreApplication {
    override fun toStringHelper(): ToStringHelper = SharedTestObjects.toStringHelper

    override fun clientVersionConfig(): ClientVersionConfigHolder = SharedTestObjects.clientVersionConfigHolder

    override fun oAuthConfig(): OAuthConfig = SharedTestObjects.oAuthConfig
}
