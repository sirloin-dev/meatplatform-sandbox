/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.util

import java.net.InetAddress

/**
 * @since 2022-12-29
 */
interface IpAddressValidatorMixin {
    fun InetAddress.isAcceptableIpAddress(newIpAddress: InetAddress): Boolean {
        // TO-DO-20221229: IP 주소 인증 필요시 실제로 구현 (지역, 국가 등을 detect 해야할 수 있다)
        return true
    }
}
