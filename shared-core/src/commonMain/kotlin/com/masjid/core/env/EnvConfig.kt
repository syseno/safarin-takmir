package com.masjid.core.env

/**
 * Environment configuration abstraction.
 * No hardcoded URLs — all values come from concrete implementations.
 */
interface EnvConfig {
    val baseUrl: String
    val isDebug: Boolean
    val name: String
}

data class DevEnvConfig(
    override val baseUrl: String = "https://7601-2402-8780-1012-3648-5073-3922-d32c-baed.ngrok-free.app/api",
    override val isDebug: Boolean = true,
    override val name: String = "dev"
) : EnvConfig

data class StagingEnvConfig(
    override val baseUrl: String = "https://staging-api.masjid.id/api",
    override val isDebug: Boolean = true,
    override val name: String = "staging"
) : EnvConfig

data class ProdEnvConfig(
    override val baseUrl: String = "https://api.masjid.id/api",
    override val isDebug: Boolean = false,
    override val name: String = "prod"
) : EnvConfig
