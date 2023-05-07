/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.common

import net.meatplatform.sandbox.domain.auth.ProviderAuthentication
import net.meatplatform.sandbox.domain.user.User

/**
 * @since 2023-05-08
 */
interface AddIpAddressAuthenticationMixin {
    // TO-DO-20221225: IPv6 케이스는 대응하지 않음. 추후 ipAddressStr 의 타입이 InetAddress 가 되어야 한다.
    fun User.addIpAddressAuthentication(ipAddressStr: String): User {
        val containsSameIpAuth = authentications.any {
            it.type == ProviderAuthentication.Type.IP_ADDRESS && it.providerId == ipAddressStr
        }

        return if (containsSameIpAuth) {
            this
        } else {
            mutator().copyUser(authentications = authentications.toMutableList().apply {
                add(
                    ProviderAuthentication.create(
                        type = ProviderAuthentication.Type.IP_ADDRESS,
                        providerId = ipAddressStr
                    )
                )
            })
        }
    }
}
