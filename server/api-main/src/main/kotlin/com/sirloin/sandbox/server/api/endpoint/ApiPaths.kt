/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.api.endpoint

import com.sirloin.sandbox.server.api.endpoint.v1.ApiPathsV1

object ApiPaths {
    /** Used by Spring default */
    const val ERROR = "/error"

    const val LATEST_VERSION = ApiPathsV1.V1
}
