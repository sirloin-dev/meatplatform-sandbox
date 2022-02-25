/*
 * sirloin-sandbox-server
 * Distributed under CC BY-NC-SA
 */
package com.sirloin.sandbox.server.core

interface CoreApplication {
    companion object {
        // Compile-time constant 가 되어야 하기 때문에 어쩔 수 없이 문자열로 지정
        const val PACKAGE_NAME = "com.sirloin.sandbox.server.core"
    }
}
