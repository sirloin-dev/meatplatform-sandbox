/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.large.endpoint.v1

import io.restassured.specification.RequestSpecification
import org.springframework.restdocs.payload.FieldDescriptor
import test.large.endpoint.v1.errorResponseFieldsSnippet

class UserTestBaseV1 : LargeTestBaseV1() {
    fun RequestSpecification.withDocumentation(
        reqFields: List<FieldDescriptor>? = null,
        respFields: List<FieldDescriptor>? = null,
    ): RequestSpecification =
        this.withDocumentation(DOCUMENT_PREFIX, reqFields, respFields)


    fun RequestSpecification.withErrorDocumentation(
        reqFields: List<FieldDescriptor>? = null
    ): RequestSpecification =
        withDocumentation(DOCUMENT_PREFIX, reqFields, errorResponseFieldsSnippet())

    companion object {
        const val DOCUMENT_PREFIX = "user"
    }
}
