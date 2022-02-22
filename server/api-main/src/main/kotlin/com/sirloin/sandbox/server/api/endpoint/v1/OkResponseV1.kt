/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.endpoint.v1

data class OkResponseV1<T>(override val body: T?) : ResponseEnvelopeV1<T>(Type.OK)
