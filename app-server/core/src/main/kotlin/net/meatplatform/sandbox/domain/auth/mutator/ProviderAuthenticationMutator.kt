/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.domain.auth.mutator

import net.meatplatform.sandbox.domain.auth.ProviderAuthentication

/**
 * @since 2023-05-06
 */
data class ProviderAuthenticationMutator(
    override val type: ProviderAuthentication.Type,
    override val providerId: String,
    override val password: String?,
    override val name: String
) : ProviderAuthentication {
    companion object {
        fun from(src: ProviderAuthentication): ProviderAuthenticationMutator = with(src) {
            if (this is ProviderAuthenticationMutator) {
                this
            } else {
                ProviderAuthenticationMutator(
                    type = type,
                    providerId = providerId,
                    password = password,
                    name = name
                )
            }
        }
    }
}
