package com.masjid.core.network

import com.masjid.core.env.EnvConfig
import com.masjid.core.security.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Factory to create configured Ktor HttpClient instances.
 * - JWT bearer auth via TokenManager
 * - Timeout configuration (connect=15s, request=30s, socket=15s)
 * - JSON content negotiation
 * - Conditional debug logging (no sensitive data in prod)
 */
object KtorClientFactory {
    fun create(
        env: EnvConfig,
        tokenManager: TokenManager,
        httpClientEngine: io.ktor.client.engine.HttpClientEngine
    ): HttpClient {
        return HttpClient(httpClientEngine) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = env.isDebug
                    isLenient = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }

            install(HttpTimeout) {
                connectTimeoutMillis = 15_000
                requestTimeoutMillis = 30_000
                socketTimeoutMillis = 15_000
            }

            if (env.isDebug) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            // Filter out sensitive headers in logs
                            val sanitized = message.replace(
                                Regex("(Authorization:\\s*Bearer\\s*)\\S+"),
                                "$1[REDACTED]"
                            )
                            println("KTOR [${env.name}]: $sanitized")
                        }
                    }
                    level = LogLevel.HEADERS
                }
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val token = tokenManager.getToken()
                        if (token != null) {
                            io.ktor.client.plugins.auth.providers.BearerTokens(token, "")
                        } else null
                    }
                    sendWithoutRequest { request ->
                        true
                    }
                }
            }

            defaultRequest {
                url(env.baseUrl)
                header("Accept", "application/json")
                contentType(ContentType.Application.Json)
            }
        }
    }
}
