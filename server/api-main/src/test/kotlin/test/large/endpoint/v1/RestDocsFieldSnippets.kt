/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package test.large.endpoint.v1

import com.sirloin.sandbox.server.api.endpoint.v1.ErrorResponseV1
import com.sirloin.sandbox.server.api.endpoint.v1.ResponseEnvelopeV1
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import kotlin.reflect.KProperty

fun basicResponseFieldsSnippet(): List<FieldDescriptor> = listOf(
    fieldWithPath(ResponseEnvelopeV1<*>::type.asRequestField())
        .type(JsonFieldType.STRING)
        .description(ResponseEnvelopeV1.DESC_TYPE),
    fieldWithPath(ResponseEnvelopeV1<*>::timestamp.asRequestField())
        .type(JsonFieldType.STRING)
        .description(ResponseEnvelopeV1.DESC_TIMESTAMP),
    fieldWithPath(ResponseEnvelopeV1<*>::body.asRequestField())
        .type(JsonFieldType.OBJECT)
        .description(ResponseEnvelopeV1.DESC_BODY)
)

fun errorResponseFieldsSnippet(): List<FieldDescriptor> = ArrayList(basicResponseFieldsSnippet()) + listOf(
    fieldWithPath(ErrorResponseV1.Body::message.asPrefixedRequestField("body"))
        .type(JsonFieldType.STRING)
        .description(ErrorResponseV1.DESC_BODY_MESSAGE),
    fieldWithPath(ErrorResponseV1.Body::code.asPrefixedRequestField("body"))
        .type(JsonFieldType.STRING)
        .description(ErrorResponseV1.DESC_BODY_CODE)
)

fun KProperty<Any?>.asRequestField(): String =
    this.asPrefixedRequestField("")

fun KProperty<Any?>.asPrefixedRequestField(prefix: String = ""): String = if (prefix.isEmpty()) {
    this.name
} else {
    "${prefix}.${this.name}"
}
