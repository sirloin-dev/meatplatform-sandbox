/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.domain.usecase.user

import io.kotest.matchers.shouldBe
import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.model.user.User
import net.meatplatform.sandbox.domain.usecase.user.CreateUserUseCase
import org.junit.jupiter.api.Assertions.assertAll

fun expectCreatedUser(actual: User, isReflecting: CreateUserUseCase.Message) {
    assertAll(
        { actual.nickname shouldBe isReflecting.nickname },
        { actual.profileImageUrl shouldBe isReflecting.profileImageUrl },
        {
            actual.authentications[0].type shouldBe isReflecting.authenticationType

            if (isReflecting.authenticationType == ProviderAuthentication.Type.EMAIL_AND_PASSWORD) {
                actual.authentications[0].providerId shouldBe isReflecting.email
            } // else 일때는 외부 시스템이 provider auth id 에 해당하는 인증정보를 뭘 줄지 모르기 때문에 검사하지 않음
        }
    )
}
