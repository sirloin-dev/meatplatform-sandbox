/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.endpoint.v1

import net.meatplatform.sandbox.endpoint.v1.ResponseEnvelopeV1

fun ResponseEnvelopeV1.Type.Companion.from(code: String?): ResponseEnvelopeV1.Type =
    ResponseEnvelopeV1.Type.values().firstOrNull { it.code == code }
        ?: throw IllegalArgumentException("Response type '$code' 에 해당하는 ResponseEnvelopeV1.Type 판단 규칙이 없습니다.")
