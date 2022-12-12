/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.endpoint.v1.user

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import net.meatplatform.sandbox.endpoint.v1.user.common.SimpleUserResponse
import net.meatplatform.sandbox.endpoint.v1.user.create.CreateUserRequest

fun assertSimpleUserResponse(actual: SimpleUserResponse, isReflecting: CreateUserRequest) {
    assertSoftly {
        actual.nickname shouldBe isReflecting.nickname
        actual.profileImageUrl shouldBe isReflecting.profileImageUrl
    }
}
