/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.jpa.repository.auth.internal

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import net.meatplatform.sandbox.annotation.OpenForTesting
import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.exception.external.auth.ProviderAuthenticationFailedException
import net.meatplatform.sandbox.http.SandboxHttpClient

/**
 * @since 2023-05-07
 */
@OpenForTesting
internal class GoogleOAuthVerifier(
    private val oAuthUrl: String,
    private val httpClient: SandboxHttpClient,
    private val objectMapper: ObjectMapper
) {
    fun verify(token: String): ProviderAuthentication {
        val response = httpClient.get(
            url = oAuthUrl,
            headers = mapOf("Authorization" to token)
        )

        val responseBody = response.body
        if (response.code == 200 && responseBody != null) {
            val googleOAuthResponse = responseBody.use { objectMapper.readValue(it, GoogleResponse::class.java) }

            return ProviderAuthentication.create(
                ProviderAuthentication.Type.GOOGLE,
                googleOAuthResponse.id.toString()
            )
        } else {
            val message = if (responseBody == null) {
                "Empty response from Google OAuth server."
            } else {
                "Unexpected response from Google OAuth server: ${response.code}"
            }

            throw ProviderAuthenticationFailedException(ProviderAuthentication.Type.GOOGLE, token, message)
        }
    }

    @JsonDeserialize
    private data class GoogleResponse(
        @JsonProperty("id")
        val id: Long,

        @JsonProperty("expires_in")
        val expiresIn: Int,

        @JsonProperty("app_id")
        val appId: Int
    )
}
