/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.util

/**
 * Enum 의 Symbol 을 그대로 외부 시스템에 입출력할 경우 Symbol name 을 바꾸기 어렵기 때문에,
 * 직렬화 및 역직렬화에 사용할 대체 값을 지정할 때 활용합니다.
 * 이는 [Enum.ordinal] 을 함부로 쓸 수 없는 이유와 같습니다.
 *
 * @since 2022-02-14
 */
interface SerializableEnum<T> {
    val code: T
}

interface EnumDeserializer<T, E : Enum<E>> {
    fun from(code: T?): E?

    fun fromOrDefault(code: T?, defaultValue: E): E = from(code) ?: defaultValue
}
