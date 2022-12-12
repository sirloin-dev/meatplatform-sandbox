/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.endpoint.v1

/**
 * @since 2022-02-14
 */
@ResponseV1
data class OkResponseV1<T>(override val body: T?) : ResponseEnvelopeV1<T>(Type.OK)
