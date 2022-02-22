/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.advice

import com.sirloin.sandbox.server.core.exception.MtExceptionCode
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

/**
 * 비즈니스 로직의 오류 코드인 [MtExceptionCode] 를 Http Status Code 로 변경하는 규칙 인터페이스
 *
 * @since 2022-02-14
 */
interface ExceptionCodeToHttpStatusConverter {
    fun convert(code: MtExceptionCode): HttpStatus?
}

@Component
internal class ExceptionCodeToHttpStatusConverterImpl : ExceptionCodeToHttpStatusConverter {
    private val conversionMap = HashMap<MtExceptionCode, HttpStatus>().apply {
        // HTTP 400: Bad request
        put(MtExceptionCode.WRONG_INPUT, HttpStatus.BAD_REQUEST)

        // HTTP 404: Not Found
        put(MtExceptionCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND)
        put(MtExceptionCode.MALFORMED_INPUT, HttpStatus.NOT_FOUND)

        // HTTP 415: Unsupported Media Type
        put(MtExceptionCode.WRONG_PRESENTATION, HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    } as Map<MtExceptionCode, HttpStatus>

    override fun convert(code: MtExceptionCode): HttpStatus? =
        conversionMap[code]
}
