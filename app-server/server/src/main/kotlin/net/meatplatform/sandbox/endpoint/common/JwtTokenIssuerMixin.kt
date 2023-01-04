/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.RSASSASigner
import jakarta.servlet.http.HttpServletResponse
import net.meatplatform.sandbox.domain.auth.AuthenticationTokenPayload
import net.meatplatform.sandbox.domain.user.User
import net.meatplatform.sandbox.domain.auth.usecase.CreateAccessTokenUseCase
import net.meatplatform.sandbox.endpoint.v1.auth.LoginController
import java.math.BigDecimal
import java.math.BigInteger

/**
 * @since 2022-12-29
 */
interface JwtTokenIssuerMixin {
    val tokenBusiness: CreateAccessTokenUseCase
    val objectMapper: ObjectMapper

    fun HttpServletResponse.issueTokenPairsOf(user: User) {
        val (accessToken, refreshToken) = tokenBusiness.createTokenOf(user)

        setHeader(LoginController.HEADER_AUTHORIZATION, accessToken.toJwtTokenBy(objectMapper))
        setHeader(LoginController.HEADER_X_AUTHORIZATION_RESPONSE, refreshToken.toJwtTokenBy(objectMapper))
    }

    private fun AuthenticationTokenPayload.toJwtTokenBy(objectMapper: ObjectMapper): String {
        val jwsHeader =
            JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT)
                .keyID(certificate.id.toString()).build()
        val jsonObject = objectMapper.createObjectNode().apply {
            serialise().forEach { (k, v) ->
                putAny(k, v)
            }
        }

        val payloadStr = objectMapper.writeValueAsString(jsonObject)

        return JWSObject(jwsHeader, Payload(payloadStr)).run {
            sign(RSASSASigner(certificate.privateKey))
            return@run serialize()
        }
    }

    // JSON allowed type 만 put 한다고 가정한다.
    private fun ObjectNode.putAny(key: String, value: Any) {
        when (value) {
            is Byte -> put(key, value.toInt())
            is Boolean -> put(key, value)
            is Short -> put(key, value.toShort())
            is Int -> put(key, value.toInt())
            is Long -> put(key, value.toLong())
            is Float -> put(key, value.toFloat())
            is Double -> put(key, value.toDouble())
            is String -> put(key, value.toString())
            is BigDecimal -> put(key, value)
            is BigInteger -> put(key, value)
            else -> throw IllegalArgumentException("Not a JSON primitive type '${value::class}' for key '$key'")
        }
    }
}
