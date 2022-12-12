/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.large

import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.ObjectMapperConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.http.Header
import io.restassured.specification.RequestSpecification
import net.meatplatform.sandbox.SandboxApplication
import net.meatplatform.sandbox.SandboxApplication.Companion.DEFAULT_APP_VERSION
import net.meatplatform.sandbox.advice.UserAgentParser.APP_NAME
import net.meatplatform.sandbox.advice.UserAgentParser.UA_DELIMITER
import net.meatplatform.sandbox.util.ClientDevicePlatform
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration
import org.springframework.restdocs.snippet.IgnorableDescriptor
import org.springframework.restdocs.snippet.Snippet
import test.TestContainerMariaDBConfig
import test.com.sirloin.annotation.LargeTest
import java.util.concurrent.atomic.AtomicInteger
import org.yaml.snakeyaml.Yaml

/**
 * API 를 테스트할 때, 번거로운 환경설정을 상속으로 해결할 수 있도록 하는 Template Class
 * 코드 공유를 위한 상속이므로 좋은 패턴은 아님
 *
 * 이 프로젝트는 Controller 가 하는 일이 많지 않습니다. 따라서 API Controller test 는 가급적 이 클래스를 이용해
 * 최대한 mock 을 배제한 상태로 시나리오를 수행하는 형태로 작성해 주세요.
 *
 * @since 2022-02-14
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [
        SandboxApplication::class,
        ApiLargeTestConfig::class,
        TestContainerMariaDBConfig::class
    ]
)
@ExtendWith(RestDocumentationExtension::class)
@LargeTest
class WebMvcRestAssuredLargeTestBase {
    val applicationContext: ApplicationContext
        get() {
            if (::_applicationContext.isInitialized) {
                return _applicationContext
            } else {
                throw IllegalStateException("Test context 가 없습니다. Spring boot test 를 올바르게 실행했나요?")
            }
        }

    val defaultObjMapper: ObjectMapper
        get() = _defaultObjMapper

    @Autowired
    private lateinit var _defaultObjMapper: ObjectMapper

    @Autowired
    private lateinit var _applicationContext: ApplicationContext
    private lateinit var testInfo: TestInfo
    private lateinit var restDocumentation: RestDocumentationContextProvider

    @Value("\${local.server.port}")
    private var port: Int = DEFAULT_PORT
    private val userAgent =
        "$APP_NAME/$DEFAULT_APP_VERSION-test+1$UA_DELIMITER " +
                "${ClientDevicePlatform.WEB.code}/en-GB Europe/London"

    @BeforeEach
    fun setup(testInfo: TestInfo, restDocumentation: RestDocumentationContextProvider) {
        this.testInfo = testInfo
        this.restDocumentation = restDocumentation
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
            ObjectMapperConfig().jackson2ObjectMapperFactory { _, _ -> _defaultObjMapper }
        )
    }

    fun jsonRequest(
        userAgent: String? = null,
        queryParameter: Map<String, Any> = mutableMapOf()
    ): RequestSpecification =
        RestAssured.given(
            RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build()
                .log().all()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON.toString())
                .accept(MediaType.APPLICATION_JSON.toString())
                .userAgent(userAgent ?: this.userAgent)
                .queryParams(queryParameter)
        )

    /**
     * 헤더가 동일한 키인 경우 먼저 설정한 헤더가 적용된다.
     * 직접 설정하는 User-Agent 값이 존재할 경우, 해당 메소드를 통해 기본 값보다 먼저 설정한다.
     */
    private fun RequestSpecification.userAgent(userAgent: String?): RequestSpecification = if (userAgent == null) {
        this
    } else {
        this.header(Header(HttpHeaders.USER_AGENT, userAgent))
    }

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
        reqFields: List<IgnorableDescriptor<*>>? = null,
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

                return@run if (prefix.isEmpty()) {
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
                Preprocessors.preprocessRequest(
                    Preprocessors.prettyPrint(),
                    Preprocessors.modifyUris().host(DEFAULT_HOST).removePort()
                ),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                *(ArrayList<Snippet>().apply {
                    reqFields?.takeIf { it.isNotEmpty() }?.let { ignorableDescriptor ->
                        ignorableDescriptor.filterIsInstance<FieldDescriptor>().takeIf { it.isNotEmpty() }
                            ?.run { add(requestFields(this)) }
                    }
                    respFields?.takeIf { it.isNotEmpty() }?.let { add(responseFields(it)) }
                }.toTypedArray())
            )
        )
    }

    companion object {
        const val DEFAULT_HOST = "localhost"
        const val DEFAULT_PORT = 8080
        private val TEST_INDICES = HashMap<String, AtomicInteger>()

        /**
         * Spring 의 Infra 설정이 TestContainer 보다 항상 우선하는 문제가 있어
         * 아예 static timing 에 컨테이너를 띄워버리고 시작하도록 설정
         * 이 때문에 TestContainers 에서 권장하는 log 설정이 제대로 동작하지 않는 문제가 있음.
         *
         * https://www.testcontainers.org/modules/databases/mysql/
         * 문서대로 "tc:" 설정을 적용할 수 있으면 간편한데, 실행환경 CPU Architecture 에 따라서
         * container 이름을 바꿔야 하는 문제가 있다.
         *
         * 통합 Docker image 가 올라오거나 하면 그때는 이런 추가설정 대신 yml 만으로 테스트 실행할 수 있는 환경
         * 만들어 두는게 좋다.
         */
        init {
            val config = SandboxApplication::class.java.classLoader.getResourceAsStream("application.yml").use {
                return@use Yaml().load<Map<String, *>>(it)
            }

            @Suppress("UNCHECKED_CAST")
            val datasourceConfig = (config["spring"] as Map<String, *>)["datasource"] as Map<String, *>
            TestContainerMariaDBConfig.forceStartContainer(datasourceConfig)
        }
    }
}
