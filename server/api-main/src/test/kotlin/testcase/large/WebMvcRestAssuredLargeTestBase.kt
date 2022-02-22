/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package testcase.large

import com.fasterxml.jackson.databind.ObjectMapper
import com.sirloin.sandbox.server.api.Application
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.ObjectMapperConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration
import org.springframework.restdocs.snippet.Snippet
import test.com.sirloin.annotation.LargeTest
import java.util.concurrent.atomic.AtomicInteger

/**
 * API 를 테스트할 때, 번거로운 환경설정을 상속으로 해결할 수 있도록 하는 Template Class
 * 코드 공유를 위한 상속이므로 좋은 패턴은 아님
 *
 * 이 프로젝트는 Controller 가 하는 일이 많지 않습니다. 따라서 API Controller test 는 가급적 이 클래스를 이용해
 * 최대한 mock 을 배제한 상태로 시나리오를 수행하는 형태로 작성해 주세요. 내가 작성한 API 가 클라이언트의 입장에서
 * 친절한지, 불친절한지를 직접 느껴 보시기 바랍니다.
 *
 * @since 2022-02-14
 */
@LargeTest
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [
        Application::class, LargeTestConfig::class
    ]
)
@ExtendWith(RestDocumentationExtension::class)
class WebMvcRestAssuredLargeTestBase {
    protected val defaultObjMapper: ObjectMapper
        get() = _defaultObjMapper

    @Autowired
    private lateinit var _defaultObjMapper: ObjectMapper

    private lateinit var testInfo: TestInfo
    private lateinit var restDocumentation: RestDocumentationContextProvider

    @LocalServerPort
    private var port: Int = DEFAULT_PORT

    @BeforeEach
    fun setup(testInfo: TestInfo, restDocumentation: RestDocumentationContextProvider) {
        this.testInfo = testInfo
        this.restDocumentation = restDocumentation
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
            ObjectMapperConfig().jackson2ObjectMapperFactory { _, _ -> _defaultObjMapper }
        )
    }

    fun jsonRequest(): RequestSpecification = given(
        RequestSpecBuilder()
            .addFilter(documentationConfiguration(restDocumentation))
            .build()
            .log().all()
            .port(port)
            .contentType(MediaType.APPLICATION_JSON.toString())
            .accept(MediaType.APPLICATION_JSON.toString())
//            .header(Header(HttpHeaders.USER_AGENT,
//                "${PlatformType.WEB.userAgentName}; $TEST_CLIENT_VERSION; $platformInfo"))
    )

    /**
     * 이 extension function 을 이용해 문서를 자동으로 생성하는 경우, Class name 및 method name 이 documentation id 가 됩니다.
     *
     * 1. 주의 1: Kotlin 이 허용하는 이름이라 하더라도 테스트 실행 시스템의 특성에 따라 snippet 파일을 생성할 수 없는 경우가 있습니다.
     * 따라서 이 extension function 을 이용하는 테스트에서는 문서화를 쉽게 하기 위해
     * Class 와 Method 의 이름들은 되도록 alphanumeric 으로 지어주시기 바랍니다.
     *
     * 2. 주의 2: Parameterized Test 인 경우에는 실행 순서에 따라 index 가 자동으로 붙습니다. 가령 다음과 같은 테스트 코드는,
     *    ```
     *    @ParameterizedTest
     *    @ValueSource(strings = ["a", "b", "c"])
     *    fun stringInput(value: String)
     *    ```
     *    각각 `stringInput#1`, `stringInput#2`, `stringInput#3` 이라는 API 문서를 생성합니다.
     */
    fun RequestSpecification.withDocumentation(
        prefix: String = "",
        reqFields: List<FieldDescriptor>? = null,
        respFields: List<FieldDescriptor>? = null,
    ): RequestSpecification {
        // JUnit spec 상 Test class 와 method 가 무조건 있어야 하니까 dangerous operation 수행
        val (defaultDocumentId, isParameterised) = with(testInfo) {
            val testGroup = testClass.get().run {
                val baseName = if (packageName.isEmpty()) {
                    canonicalName
                } else {
                    canonicalName.substring(packageName.length + 1)
                }

                return@run if(prefix.isEmpty()) {
                    baseName
                } else {
                    "${prefix}/${baseName}"
                }
            }.replace(".", "/")

            val method = testMethod.get()
            val testName = method.name

            return@with "${testGroup}/${testName}" to (method.getAnnotation(ParameterizedTest::class.java) != null)
        }

        // Parameterized test 인 경우 document id 뒤에 index 를 증가시켜 준다.
        val documentId = if (isParameterised) {
            val index = TEST_INDICES[defaultDocumentId] ?: run {
                val initialIndex = AtomicInteger(1)
                TEST_INDICES[defaultDocumentId] = initialIndex
                return@run initialIndex
            }

            "${defaultDocumentId}#${index.getAndIncrement()}"
        } else {
            defaultDocumentId
        }

        return this.filter(
            document(
                documentId,
                preprocessRequest(prettyPrint(), modifyUris().host(DEFAULT_HOST).removePort()),
                preprocessResponse(prettyPrint()),
                *(ArrayList<Snippet>().apply {
                    reqFields?.takeIf { it.isNotEmpty() }?.let { add(requestFields(it)) }
                    respFields?.takeIf { it.isNotEmpty() }?.let { add(responseFields(it)) }
                }.toTypedArray())
            )
        )
    }

    companion object {
        // Documentation 은 Alpha 서버에만 올릴거니까, Alpha 서버의 Host 이름을 여기 적어주면 됩니다.
        const val DEFAULT_HOST = "localhost"
        const val DEFAULT_PORT = 8080

        private val TEST_INDICES = HashMap<String, AtomicInteger>()
    }
}
