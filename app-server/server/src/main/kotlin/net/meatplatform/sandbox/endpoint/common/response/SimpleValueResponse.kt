/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.common.response

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import net.meatplatform.sandbox.endpoint.v1.ResponseV1

/**
 * @since 2023-05-07
 */
@ResponseV1
@JsonSerialize
data class SimpleValueResponse<T>(
    val value: T
)
