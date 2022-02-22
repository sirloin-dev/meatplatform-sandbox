/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.advice.responseDecorator

import com.sirloin.sandbox.server.api.endpoint.v1.ResponseEnvelopeV1
import com.sirloin.sandbox.server.api.endpoint.v1.ResponseV1
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

/**
 * [ResponseV1] 어노테이션으로 감싼 컨트롤러의 반환값을
 * [ResponseEnvelopeV1] 로 감싸주는 역할을 합니다. Any type 에 동작하기 때문에, supports 조건 변경시 주의해야 합니다.
 *
 * @since 2022-02-14
 */
@RestControllerAdvice
class V1ResponseDecorator : ResponseBodyAdvice<Any> {
    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        val methodReturnType = returnType.method?.returnType ?: return false
        return methodReturnType.declaredAnnotations.any {
            it.annotationClass == ResponseV1::class
        }
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? =
        ResponseEnvelopeV1.ok(body)
}
