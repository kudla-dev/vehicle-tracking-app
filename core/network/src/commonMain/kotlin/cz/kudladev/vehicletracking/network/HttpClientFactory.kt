package cz.kudladev.vehicletracking.network

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class HttpClientFactory(
    private val sessionManager: AuthSessionManager,
    private val apiConfig: ApiConfig,
) {

    fun create(httpClientEngine: HttpClientEngine): HttpClient {
        return HttpClient(httpClientEngine) {
            apiEndpoint(apiConfig)

            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                sanitizeHeader { it == HttpHeaders.Authorization }
                level = LogLevel.INFO
            }
            install(Auth) {
                bearer {
                    sendWithoutRequest { apiConfig.contains(it.url.build()) }
                    loadTokens {
                        val (accessToken, refreshToken) = sessionManager.loadTokens()
                        if (accessToken.isNullOrBlank()) {
                            null
                        } else {
                            BearerTokens(accessToken, refreshToken ?: "")
                        }
                    }

                    refreshTokens {
                        val refreshToken = oldTokens?.refreshToken
                            ?: sessionManager.loadTokens().second
                        if (refreshToken.isNullOrBlank()) {
                            return@refreshTokens null
                        }

                        val refreshed = sessionManager.refreshTokens(refreshToken, this.client)
                        if (refreshed != null) {
                            BearerTokens(refreshed.first, refreshed.second)
                        } else {
                            null
                        }
                    }
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30000  // 30 seconds
                connectTimeoutMillis = 15000  // 15 seconds
            }

        }
    }
}
