/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.large.endpoint.v1

import io.restassured.specification.RequestSpecification
import net.meatplatform.sandbox.domain.repository.auth.ProviderAuthRepository
import org.springframework.http.HttpHeaders
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.snippet.IgnorableDescriptor
import test.domain.repository.auth.SpyProviderAuthRepository
import testcase.large.JsonRequestAssertionsMixin
import testcase.large.WebMvcRestAssuredLargeTestBase
import java.net.URI

/**
 * @since 2022-02-14
 */
class LargeTestBaseV1 : WebMvcRestAssuredLargeTestBase(), JsonRequestAssertionsMixin, AuthBeansOverrideMixin {
    protected open val documentPrefix: URI? = null

//    fun RequestSpecification.withDocumentation(
//        reqFields: List<IgnorableDescriptor<*>>? = null,
//        respFields: List<FieldDescriptor>? = null,
//    ): RequestSpecification =
//        super.withDocumentation(documentPrefix.toRelativePath(), reqFields, respFields)
//
//    fun RequestSpecification.withErrorDocumentation(
//        reqFields: List<FieldDescriptor>? = null
//    ): RequestSpecification =
//        super.withDocumentation(documentPrefix.toRelativePath(), reqFields, errorResponseFieldsSnippet())

    fun RequestSpecification.authenticatedBy(token: String): RequestSpecification =
        header(HttpHeaders.AUTHORIZATION, "Bearer $token")

    private fun URI?.toRelativePath(): String {
        if (this == null) {
            throw UnsupportedOperationException("Test 문서를 작성하려면 `documentPrefix` 가 null 이 아니어야 합니다.")
        }

        return toString().let {
            if (it.startsWith("/")) {
                if (it.length == 1) {
                    ""
                } else {
                    it.substring(1)
                }
            } else {
                it
            }
        }
    }
}
