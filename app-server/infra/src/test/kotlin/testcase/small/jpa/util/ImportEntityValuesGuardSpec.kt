/*
 * bondaero-server
 * Sir.LOIN Intellectual property. All rights reserved.
 */
package testcase.small.jpa.util

import com.sirloin.jvmlib.util.EMPTY_UUID
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import net.meatplatform.sandbox.jpa.util.ImportEntityValuesGuard
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import test.SharedTestObjects.faker
import test.com.sirloin.annotation.SmallTest
import test.util.randomAlphanumeric
import java.util.*
import java.util.stream.Stream

/**
 * @since 2023-04-15
 */
@SmallTest
class ImportEntityValuesGuardSpec {
    @DisplayName("right value 가 null 이면 자기 자신을 반환한다.")
    @Test
    fun nothingHappensIfRightValuesAreNull() {
        // given:
        val original = ObjectWithUUID.random()

        // then:
        val notChanged = with(original) {
            importIfAllowed(null, ObjectWithUUID::id, ObjectWithUUID.VALUE_IMPORTER)
        }

        // expect:
        notChanged shouldBe original
    }

    @DisplayName("id 가 UUID 타입인 객체인 경우:")
    @Nested
    inner class ObjectsHavingUUID {
        @DisplayName("left 의 id 가 null or EMPTY 면 id 비교 없이 right 를 그냥 import 한다.")
        @Test
        fun importsUnconditionallyIfLeftIdIsNullOrEmpty() {
            // given:
            val original = ObjectWithUUID.random()
            val values = ObjectWithUUID.random(id = UUID.randomUUID())
            val expected = original.copy(value = values.value)

            // then:
            val imported = with(original) {
                importIfAllowed(values, ObjectWithUUID::id, ObjectWithUUID.VALUE_IMPORTER)
            }

            // expect:
            imported shouldBe expected
        }

        @ParameterizedTest(name = "right value 의 id 가 {0} 이면 import 한다.")
        @MethodSource("testcase.small.jpa.util.ImportEntityValuesGuardSpec#importableEmptyUuids")
        fun importIfRightIdIsEmpty(
            @Suppress("UNUSED_PARAMETER", "LocalVariableName") _testName: String,
            id: UUID?
        ) {
            // given:
            val original = ObjectWithUUID.random()
            val values = ObjectWithUUID.random(id)
            val expected = original.copy(value = values.value)

            // then:
            val imported = with(original) {
                importIfAllowed(values, ObjectWithUUID::id, ObjectWithUUID.VALUE_IMPORTER)
            }

            // expect:
            imported shouldBe expected
        }

        @ParameterizedTest(name = "left 와 right 의 id 가 같으면 right value 를 import 한다(id = {0}).")
        @MethodSource("testcase.small.jpa.util.ImportEntityValuesGuardSpec#importableSameUuids")
        fun importIfRightIdIsEqual(id: UUID?) {
            // given:
            val original = ObjectWithUUID.random(id = id)
            val values = ObjectWithUUID.random(id = id)
            val expected = original.copy(value = values.value)

            // then:
            val imported = with(original) {
                importIfAllowed(values, ObjectWithUUID::id, ObjectWithUUID.VALUE_IMPORTER)
            }

            // expect:
            imported shouldBe expected
        }

        @DisplayName("right value 의 id 가 left value 의 id 와 다르면 IllegalArgumentException 이 발생한다.")
        @Test
        fun errorIfIdsAreDifferent() {
            // given:
            val original = ObjectWithUUID.random(id = UUID.randomUUID())
            val values = ObjectWithUUID.random(id = UUID.randomUUID())

            // expect:
            shouldThrow<IllegalArgumentException> {
                with(original) {
                    importIfAllowed(values, ObjectWithUUID::id, ObjectWithUUID.VALUE_IMPORTER)
                }
            }
        }
    }

    @DisplayName("id 가 Int 타입인 객체인 경우:")
    @Nested
    inner class ObjectsHavingIntId {
        @DisplayName("left 의 id 가 invalid JPA id 면 id 비교 없이  right 를 그냥 import 한다.")
        @Test
        fun importsUnconditionallyIfLeftIdIsNullOrEmpty() {
            // given:
            val original = ObjectWithIntId.random()
            val values = ObjectWithIntId.random(id = randomInt())
            val expected = original.copy(value = values.value)

            // then:
            val imported = with(original) {
                importIfAllowed(values, ObjectWithIntId::id, ObjectWithIntId.VALUE_IMPORTER)
            }

            // expect:
            imported shouldBe expected
        }

        @ParameterizedTest(name = "right value 의 id 가 {0} 이면 import 한다.")
        @MethodSource("testcase.small.jpa.util.ImportEntityValuesGuardSpec#importableEmptyIntIds")
        fun importIfRightIdIsEmpty(id: Int?) {
            // given:
            val original = ObjectWithIntId.random()
            val values = ObjectWithIntId.random(id)
            val expected = original.copy(value = values.value)

            // then:
            val imported = with(original) {
                importIfAllowed(values, ObjectWithIntId::id, ObjectWithIntId.VALUE_IMPORTER)
            }

            // expect:
            imported shouldBe expected
        }

        @ParameterizedTest(name = "left 와 right 의 id 가 같으면 right value 를 import 한다(id = {0}).")
        @MethodSource("testcase.small.jpa.util.ImportEntityValuesGuardSpec#importableSameIntIds")
        fun importIfRightIdIsEqual(id: Int?) {
            // given:
            val original = ObjectWithIntId.random(id = id)
            val values = ObjectWithIntId.random(id = id)
            val expected = original.copy(value = values.value)

            // then:
            val imported = with(original) {
                importIfAllowed(values, ObjectWithIntId::id, ObjectWithIntId.VALUE_IMPORTER)
            }

            // expect:
            imported shouldBe expected
        }

        @DisplayName("right value 의 id 가 left value 의 id 와 다르면 IllegalArgumentException 이 발생한다.")
        @Test
        fun errorIfIdsAreDifferent() {
            // given:
            val original = ObjectWithIntId.random(id = randomInt())
            val values = ObjectWithIntId.random(id = randomInt())

            // expect:
            shouldThrow<IllegalArgumentException> {
                with(original) {
                    importIfAllowed(values, ObjectWithIntId::id, ObjectWithIntId.VALUE_IMPORTER)
                }
            }
        }
    }

    @DisplayName("id 가 Long 타입인 객체인 경우:")
    @Nested
    inner class ObjectsHavingLongId {
        @DisplayName("left 의 id 가 invalid JPA id 면 id 비교 없이  right 를 그냥 import 한다.")
        @Test
        fun importsUnconditionallyIfLeftIdIsNullOrEmpty() {
            // given:
            val original = ObjectWithLongId.random()
            val values = ObjectWithLongId.random(id = randomLong())
            val expected = original.copy(value = values.value)

            // then:
            val imported = with(original) {
                importIfAllowed(values, ObjectWithLongId::id, ObjectWithLongId.VALUE_IMPORTER)
            }

            // expect:
            imported shouldBe expected
        }

        @ParameterizedTest(name = "right value 의 id 가 {0} 이면 import 한다.")
        @MethodSource("testcase.small.jpa.util.ImportEntityValuesGuardSpec#importableEmptyLongIds")
        fun importIfRightIdIsEmpty(id: Long?) {
            // given:
            val original = ObjectWithLongId.random()
            val values = ObjectWithLongId.random(id)
            val expected = original.copy(value = values.value)

            // then:
            val imported = with(original) {
                importIfAllowed(values, ObjectWithLongId::id, ObjectWithLongId.VALUE_IMPORTER)
            }

            // expect:
            imported shouldBe expected
        }

        @ParameterizedTest(name = "left 와 right 의 id 가 같으면 right value 를 import 한다(id = {0}).")
        @MethodSource("testcase.small.jpa.util.ImportEntityValuesGuardSpec#importableSameLongIds")
        fun importIfRightIdIsEqual(id: Long?) {
            // given:
            val original = ObjectWithLongId.random(id = id)
            val values = ObjectWithLongId.random(id = id)
            val expected = original.copy(value = values.value)

            // then:
            val imported = with(original) {
                importIfAllowed(values, ObjectWithLongId::id, ObjectWithLongId.VALUE_IMPORTER)
            }

            // expect:
            imported shouldBe expected
        }

        @DisplayName("right value 의 id 가 left value 의 id 와 다르면 IllegalArgumentException 이 발생한다.")
        @Test
        fun errorIfIdsAreDifferent() {
            // given:
            val original = ObjectWithLongId.random(id = randomLong())
            val values = ObjectWithLongId.random(id = randomLong())

            // expect:
            shouldThrow<IllegalArgumentException> {
                with(original) {
                    importIfAllowed(values, ObjectWithLongId::id, ObjectWithLongId.VALUE_IMPORTER)
                }
            }
        }
    }

    @DisplayName("id 가 String 타입인 객체인 경우:")
    @Nested
    inner class ObjectsHavingStringId {
        @DisplayName("left 의 id 가 null or EMPTY 면 id 비교 없이 right 를 그냥 import 한다.")
        @Test
        fun importsUnconditionallyIfLeftIdIsNullOrEmpty() {
            // given:
            val original = ObjectWithStringId.random()
            val values = ObjectWithStringId.random(id = randomString())
            val expected = original.copy(value = values.value)

            // then:
            val imported = with(original) {
                importIfAllowed(values, ObjectWithStringId::id, ObjectWithStringId.VALUE_IMPORTER)
            }

            // expect:
            imported shouldBe expected
        }

        @ParameterizedTest(name = "right value 의 id 가 {0} 이면 import 한다.")
        @MethodSource("testcase.small.jpa.util.ImportEntityValuesGuardSpec#importableEmptyStringIds")
        fun importIfRightIdIsEmpty(
            @Suppress("UNUSED_PARAMETER", "LocalVariableName") _testName: String,
            id: String?
        ) {
            // given:
            val original = ObjectWithStringId.random()
            val values = ObjectWithStringId.random(id)
            val expected = original.copy(value = values.value)

            // then:
            val imported = with(original) {
                importIfAllowed(values, ObjectWithStringId::id, ObjectWithStringId.VALUE_IMPORTER)
            }

            // expect:
            imported shouldBe expected
        }

        @ParameterizedTest(name = "left 와 right 의 id 가 같으면 right value 를 import 한다(id = {0}).")
        @MethodSource("testcase.small.jpa.util.ImportEntityValuesGuardSpec#importableSameStringIds")
        fun importIfRightIdIsEqual(id: String?) {
            // given:
            val original = ObjectWithStringId.random(id = id)
            val values = ObjectWithStringId.random(id = id)
            val expected = original.copy(value = values.value)

            // then:
            val imported = with(original) {
                importIfAllowed(values, ObjectWithStringId::id, ObjectWithStringId.VALUE_IMPORTER)
            }

            // expect:
            imported shouldBe expected
        }

        @DisplayName("right value 의 id 가 left value 의 id 와 다르면 IllegalArgumentException 이 발생한다.")
        @Test
        fun errorIfIdsAreDifferent() {
            // given:
            val original = ObjectWithStringId.random(id = randomString())
            val values = ObjectWithStringId.random(id = randomString())

            // expect:
            shouldThrow<IllegalArgumentException> {
                with(original) {
                    importIfAllowed(values, ObjectWithStringId::id, ObjectWithStringId.VALUE_IMPORTER)
                }
            }
        }
    }

    private data class ObjectWithUUID(
        val id: UUID?,
        val value: String
    ) : ImportEntityValuesGuard<ObjectWithUUID> {
        companion object {
            val VALUE_IMPORTER: (ObjectWithUUID.(rightValues: ObjectWithUUID) -> ObjectWithUUID) = lambda@{
                return@lambda copy(value = it.value)
            }

            fun random(id: UUID? = null) = ObjectWithUUID(id, faker.lorem().sentence())
        }
    }

    private data class ObjectWithIntId(
        val id: Int?,
        val value: String
    ) : ImportEntityValuesGuard<ObjectWithIntId> {
        companion object {
            val VALUE_IMPORTER: (ObjectWithIntId.(rightValues: ObjectWithIntId) -> ObjectWithIntId) = lambda@{
                return@lambda copy(value = it.value)
            }

            fun random(id: Int? = null) = ObjectWithIntId(id, faker.lorem().sentence())
        }
    }

    private data class ObjectWithLongId(
        val id: Long?,
        val value: String
    ) : ImportEntityValuesGuard<ObjectWithLongId> {
        companion object {
            val VALUE_IMPORTER: (ObjectWithLongId.(rightValues: ObjectWithLongId) -> ObjectWithLongId) = lambda@{
                return@lambda copy(value = it.value)
            }

            fun random(id: Long? = null) = ObjectWithLongId(id, faker.lorem().sentence())
        }
    }

    private data class ObjectWithStringId(
        val id: String?,
        val value: String
    ) : ImportEntityValuesGuard<ObjectWithStringId> {
        companion object {
            val VALUE_IMPORTER: (ObjectWithStringId.(rightValues: ObjectWithStringId) -> ObjectWithStringId) = lambda@{
                return@lambda copy(value = it.value)
            }

            fun random(id: String? = null) = ObjectWithStringId(id, faker.lorem().sentence())
        }
    }

    companion object {
        private fun randomInt(): Int = faker.random().nextInt(Int.MAX_VALUE)

        // 무조건 positive long 만 나와야 함
        private fun randomLong(): Long = faker.random().nextInt(1, Int.MAX_VALUE).toLong()

        /*
         * 테스트 설계상 랜덤 문자열이라 해도 겹칠 수 있음. 따라서 확률을 극단적으로 낮춘다.
         * (62 ^ 32) = 6.5398E+53 라면 우주 전체의 원자 갯수 정도의 크기이므로 충분히 겹치지 않을 것이라고 가정한다.
        */
        private fun randomString(): String = randomAlphanumeric(32, 32)

        @JvmStatic
        fun importableEmptyUuids(): Stream<Arguments> = Stream.of(
            Arguments.of("null", null),
            Arguments.of("EMPTY_UUID", EMPTY_UUID)
        )

        @JvmStatic
        fun importableSameUuids(): Stream<UUID?> = Stream.of(null, EMPTY_UUID, UUID.randomUUID())

        @JvmStatic
        fun importableEmptyIntIds(): Stream<Int?> = Stream.of(null, 0, -1)

        @JvmStatic
        fun importableSameIntIds(): Stream<Int?> = Stream.of(null, 0, -1, randomInt())

        @JvmStatic
        fun importableEmptyLongIds(): Stream<Long?> = Stream.of(null, 0, -1L)

        @JvmStatic
        fun importableSameLongIds(): Stream<Long?> = Stream.of(null, 0, -1L, randomLong())

        @JvmStatic
        fun importableEmptyStringIds(): Stream<Arguments> = Stream.of(
            Arguments.of("null", null),
            Arguments.of("빈 문자열", "")
        )

        @JvmStatic
        fun importableSameStringIds(): Stream<String?> = Stream.of(null, "", randomString())
    }
}
