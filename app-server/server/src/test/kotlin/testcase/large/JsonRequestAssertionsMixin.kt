/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.large

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.restassured.http.Headers
import io.restassured.response.Response
import io.restassured.response.ValidatableResponse
import net.meatplatform.sandbox.endpoint.v1.ErrorResponseV1
import net.meatplatform.sandbox.endpoint.v1.ResponseEnvelopeV1
import net.meatplatform.sandbox.exception.ErrorCodeBook
import org.hamcrest.CoreMatchers.`is`
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import test.endpoint.v1.from
import test.exception.from
import java.time.Instant
import kotlin.reflect.KClass

/**
 * @since 2022-02-14
 */
interface JsonRequestAssertionsMixin {
    val defaultObjMapper: ObjectMapper

    private val log: Logger
        get() = LoggerFactory.getLogger(this::class.java)

    fun <T : Any> Response.expect2xx(
        responseType: KClass<T>,
        httpStatus: HttpStatus = HttpStatus.OK,
    ): Pair<T, Headers> {
        val ongoingResponse = this.then().assertThat().statusCode(`is`(httpStatus.value()))
        val parsed = parseResponse(ongoingResponse)

        if (parsed.type !== ResponseEnvelopeV1.Type.OK) {
            parsed.printAsError()
            throw AssertionError("OK 응답이 아닙니다. 테스트 케이스를 다시 확인하세요.")
        }

        return defaultObjMapper.convertValue(parsed.body, responseType.java) to ongoingResponse.extract().headers()
    }

    fun Response.expect4xx(
        httpStatus: HttpStatus = HttpStatus.BAD_REQUEST
    ): Pair<ErrorResponseV1.Body, Headers> {
        val code = httpStatus.value()
        if (code < 400 || code > 499) {
            throw AssertionError("4xx 응답이 아닙니다. Http status: $httpStatus")
        }

        return expectError(httpStatus)
    }

    fun Response.expect5xx(
        httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    ): Pair<ErrorResponseV1.Body, Headers> {
        val code = httpStatus.value()
        if (code < 500 || code > 599) {
            throw AssertionError("5xx 응답이 아닙니다. Http status: $httpStatus")
        }

        return expectError(httpStatus)
    }

    fun Pair<ErrorResponseV1.Body, Headers>.withExceptionCode(expectedCode: ErrorCodeBook) {
        val actual = ErrorCodeBook.from(first.code)

        if (actual != expectedCode) {
            throw AssertionError("기대한 실패 코드: $expectedCode, 실제 실패 코드: $actual")
        }
    }

    private fun Response.expectError(
        httpStatus: HttpStatus
    ): Pair<ErrorResponseV1.Body, Headers> {
        val ongoingResponse = this.then().assertThat().statusCode(`is`(httpStatus.value()))
        val parsed = parseResponse(ongoingResponse)

        if (parsed.type !== ResponseEnvelopeV1.Type.ERROR) {
            parsed.printAsError()
            throw AssertionError("ERROR 응답이 아닙니다. 테스트 케이스를 다시 확인하세요.")
        }

        return defaultObjMapper.convertValue(parsed.body, ErrorResponseV1.Body::class.java) to
                ongoingResponse.extract().headers()
    }

    private fun parseResponse(response: ValidatableResponse): ResponseEnvelopeV1<Map<String, *>> {
        val responseStr = response.extract().body().asString()
        try {
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
        } catch (e: Exception) {
            log.error("responseStr = '$responseStr'")
            throw e
        }
    }

    private fun ResponseEnvelopeV1<Map<String, *>>.printAsError() {
        log.error(defaultObjMapper.copy().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(this))
    }

    private data class ResponseEnvelopeV1Impl<T>(
        override val type: Type,
        override val timestamp: Instant,
        override val body: T?
    ) : ResponseEnvelopeV1<T>(type, timestamp)
}