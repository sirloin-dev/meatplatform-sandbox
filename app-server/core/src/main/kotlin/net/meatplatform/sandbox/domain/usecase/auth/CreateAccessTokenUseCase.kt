/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.usecase.auth

import net.meatplatform.sandbox.annotation.UseCase
import net.meatplatform.sandbox.domain.model.auth.AccessTokenPayload
import net.meatplatform.sandbox.domain.model.auth.AuthenticationTokenPayload
import net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.model.auth.RefreshTokenPayload
import net.meatplatform.sandbox.domain.model.user.User
import net.meatplatform.sandbox.domain.repository.auth.RsaCertificateRepository
import net.meatplatform.sandbox.domain.usecase.auth.CreateAccessTokenUseCase.Companion.DEFAULT_AUDIENCE_NAME
import net.meatplatform.sandbox.exception.internal.IpAuthenticationNotFoundException
import java.net.Inet4Address
import java.net.InetAddress
import java.time.Instant

/**
 * @since 2022-12-26
 */
interface CreateAccessTokenUseCase {
    /**
     * User 의 Access Token 및 Refresh Token 을 생성합니다. 이 method 를 호출하는 User 는 authentication 내에
     * [net.meatplatform.sandbox.domain.model.auth.ProviderAuthentication.Type.IP_ADDRESS] 타입의 인증정보를
     * 포함하고 있어야 합니다.
     *
     * 2022-12 현재 IPv4 인증만 허용합니다.
     */
    fun createTokenOf(user: User): Pair<AuthenticationTokenPayload, AuthenticationTokenPayload>

    companion object {
        const val DEFAULT_AUDIENCE_NAME = "meatplatform-sandbox-client"

        fun newInstance(
            issuerName: String,
            accessTokenLifeSeconds: Long,
            refreshTokenLifeSeconds: Long,
            rsaCertificateRepository: RsaCertificateRepository
        ): CreateAccessTokenUseCase = CreateAccessTokenUseCaseImpl(
            issuerName = issuerName,
            accessTokenLifeSeconds = accessTokenLifeSeconds,
            refreshTokenLifeSeconds = refreshTokenLifeSeconds,
            rsaCerts = rsaCertificateRepository
        )
    }
}

@UseCase
internal class CreateAccessTokenUseCaseImpl(
    private val issuerName: String,
    private val accessTokenLifeSeconds: Long,
    private val refreshTokenLifeSeconds: Long,
    private val rsaCerts: RsaCertificateRepository
) : CreateAccessTokenUseCase {
    override fun createTokenOf(user: User): Pair<AuthenticationTokenPayload, AuthenticationTokenPayload> {
        val ipAuthentication = user.authentications.firstOrNull {
            it.type == ProviderAuthentication.Type.IP_ADDRESS
        } ?: throw IpAuthenticationNotFoundException()

        val now = Instant.now()
        val subject = user.id.toString()
        /*
         * TO-DO-20221225: Token 을 별도 공간에 저장하지 않는다. 따라서 한번 발행한 Token 을 누군가가 도청할 경우,
         * 자연 만료 시간 전까지 이를 막을 방법이 없다.
         *
         * TO-DO-20221226: 로직 User 내로 이동.
         */
        val certificate = rsaCerts.findCurrentlyActive() ?: rsaCerts.create(rsaCerts.issueRandom())
        val accessToken = AccessTokenPayload.create(
            certificate = certificate,
            issuer = issuerName,
            audience = DEFAULT_AUDIENCE_NAME,
            subject = subject,
            issuedAt = now,
            expiredAt = now.plusSeconds(accessTokenLifeSeconds),
            ipAddress = InetAddress.getByName(ipAuthentication.providerId)
        )
        val refreshToken = RefreshTokenPayload.create(
            certificate = certificate,
            issuer = issuerName,
            audience = DEFAULT_AUDIENCE_NAME,
            subject = subject,
            issuedAt = now,
            expiredAt = now.plusSeconds(refreshTokenLifeSeconds)
        )

        return accessToken to refreshToken
    }
}
