package cz.kudladev.vehicletracking.network

import cz.kudladev.vehicletracking.core.datastore.DataStoreRepository
import cz.kudladev.vehicletracking.core.domain.auth.AuthRepository
import cz.kudladev.vehicletracking.model.onError
import cz.kudladev.vehicletracking.model.onSuccess
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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

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
                        isLenient = true
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
                            authRepository
                                .refresh(refreshToken)
                                .onSuccess {
                                    bearerTokens = BearerTokens(
                                        accessToken = dataStoreRepository.getAccessToken() ?: "",
                                        refreshToken = dataStoreRepository.getRefreshToken() ?: ""
                                    )
                                }
                                .onError {
                                    println("Error refreshing token: ${it.message}")
                                    bearerTokens = BearerTokens("","")
                                }
                            bearerTokens
                        } else {
                            BearerTokens("","")
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