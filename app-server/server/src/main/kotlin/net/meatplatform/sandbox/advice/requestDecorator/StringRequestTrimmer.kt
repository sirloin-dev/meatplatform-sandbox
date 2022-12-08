/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.advice.requestDecorator

import org.springframework.beans.propertyeditors.StringTrimmerEditor
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * String 타입 요청 앞뒤의 공백 문자열을 모두 제거합니다.
 *
 * @since 2022-02-14
 */
@RestControllerAdvice
class StringRequestTrimmer {
    @InitBinder
    fun registerStringRequestTrimmer(binder: WebDataBinder) {
        binder.registerCustomEditor(String::class.java, StringTrimmerEditor(true))
    }
}
