/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.usecase.auth

import net.meatplatform.sandbox.annotation.UseCase
import net.meatplatform.sandbox.domain.model.user.User

/**
 * @since 2022-12-26
 */
interface CreateAccessTokenUseCase {
    fun createTokenOf(user: User): Pair<String, String>

    companion object {
        fun newInstance(): CreateAccessTokenUseCase = CreateAccessTokenUseCaseImpl()
    }
}

@UseCase
internal class CreateAccessTokenUseCaseImpl : CreateAccessTokenUseCase {
    override fun createTokenOf(user: User): Pair<String, String> {
        /*
         * TO-DO-20221225: Token 을 별도 공간에 저장하지 않는다. 따라서 한번 발행한 Token 을 누군가가 도청할 경우,
         * 자연 만료 시간 전까지 이를 막을 방법이 없다.
         */
        return "" to ""
    }
}
