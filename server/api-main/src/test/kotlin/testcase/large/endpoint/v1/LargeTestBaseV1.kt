/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.large.endpoint.v1

import com.fasterxml.jackson.databind.SerializationFeature
import com.sirloin.sandbox.server.api.endpoint.v1.ErrorResponseV1
import com.sirloin.sandbox.server.api.endpoint.v1.ResponseEnvelopeV1
import com.sirloin.sandbox.server.core.exception.MtExceptionCode
import io.restassured.response.Response
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.springframework.http.HttpStatus
import org.springframework.restdocs.payload.FieldDescriptor
import test.large.endpoint.v1.errorResponseFieldsSnippet
import testcase.large.WebMvcRestAssuredLargeTestBase
import java.time.Instant
import kotlin.reflect.KClass

class LargeTestBaseV1 : WebMvcRestAssuredLargeTestBase() {
    fun <T : Any> Response.expect2xx(
        responseType: KClass<T>,
        httpStatus: HttpStatus = HttpStatus.OK,
    ): T {
        val parsed = parseResponse(this.then().assertThat().statusCode(`is`(httpStatus.value())))

        if (parsed.type !== ResponseEnvelopeV1.Type.OK) {
            parsed.printAsError()
            throw AssertionError("OK 응답이 아닙니다. 테스트 케이스를 다시 확인하세요.")
        }

        return defaultObjMapper.convertValue(parsed.body, responseType.java)
    }

    fun Response.expect4xx(httpStatus: HttpStatus = HttpStatus.BAD_REQUEST): ErrorResponseV1.Body {
        val parsed = parseResponse(this.then().assertThat().statusCode(`is`(httpStatus.value())))

        if (parsed.type !== ResponseEnvelopeV1.Type.ERROR) {
            parsed.printAsError()
            throw AssertionError("ERROR 응답이 아닙니다. 테스트 케이스를 다시 확인하세요.")
        }

        return defaultObjMapper.convertValue(parsed.body, ErrorResponseV1.Body::class.java)
    }

    fun ErrorResponseV1.Body.withExceptionCode(expected: MtExceptionCode) {
        assertThat(MtExceptionCode.from(java.lang.Long.decode(this.code)), `is`(expected))
    }

    private fun parseResponse(response: ValidatableResponse): ResponseEnvelopeV1<Map<String, *>> {
        val responseStr = response.extract().body().asString()

        /*
         * Kotlin <*, *> 타입은 <Any, Any> 로 바꿀 수 있으며, 우리 서버는 JSON 형식으로 통신하므로
         * JSON 의 contract 인 <String, Any> 타입으로 변경해도 문제는 없다.
         */
        @Suppress("UNCHECKED_CAST")
        val rawResponseMap = defaultObjMapper.readValue(responseStr, MutableMap::class.java) as Map<String, *>

        val strType = rawResponseMap["type"] as String
        // rawResponseMap 변환과 같은 이유
        @Suppress("UNCHECKED_CAST")
        val jsonBody = rawResponseMap["body"] as Map<String, *>
        val timestamp = Instant.parse(rawResponseMap["timestamp"] as String)

        return ResponseEnvelopeV1Impl(
            type = ResponseEnvelopeV1.Type.from(strType),
            timestamp = timestamp,
            body = jsonBody
        )
    }

    private fun ResponseEnvelopeV1<Map<String, *>>.printAsError() {
        System.err.println(
            defaultObjMapper.copy().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(this)
        )
    }

    private data class ResponseEnvelopeV1Impl<T>(
        override val type: Type,
        override val timestamp: Instant,
        override val body: T?
    ) : ResponseEnvelopeV1<T>(type, timestamp)
}
