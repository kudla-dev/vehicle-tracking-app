package cz.kudladev.vehicletracking.network

import cz.kudladev.vehicletracking.auth.domain.AuthRepository
import cz.kudladev.vehicletracking.datastore.DataStoreRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.koinConfiguration

class HttpClientFactory(
    private val dataStoreRepository: DataStoreRepository,
): KoinComponent {

    private val authRepository: AuthRepository by inject()


    fun create(httpClientEngine: HttpClientEngine): HttpClient{
        return HttpClient(httpClientEngine) {

            install(ContentNegotiation){
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                    }
                )
            }
            install(Logging){
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.ALL
            }
            install(Auth){
                bearer {
                    loadTokens {
                        val accessToken = dataStoreRepository.getAccessToken()
                        val refreshToken = dataStoreRepository.getRefreshToken()

                        println("Access token: $accessToken")

                        BearerTokens(accessToken ?: "", refreshToken)
                    }

                    refreshTokens {
                        val refreshToken = dataStoreRepository.getRefreshToken()

                        var bearerTokens: BearerTokens? = null

                        if (refreshToken != null) {
                            val response = authRepository.refresh(refreshToken)
                            response
                                .onSuccess {
                                    dataStoreRepository.saveAccessToken(it.accessToken)
                                    dataStoreRepository.saveRefreshToken(it.refreshToken)
                                    println("New access token: ${it.accessToken}")
                                    bearerTokens = BearerTokens(it.accessToken, it.refreshToken)
                                }
                                .onError {
                                    dataStoreRepository.clearTokens()
                                    println("Error refreshing token: ${it.message}")
                                    bearerTokens = null
                                }
                            bearerTokens
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

            defaultRequest {
                contentType(ContentType.Application.Json)
                url {
                    protocol = URLProtocol.HTTP
                    host = "0.0.0.0"
                    port = 8080
                }
            }
        }
    }

}