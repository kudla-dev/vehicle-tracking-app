package cz.kudladev.vehicletracking.core.data.auth

import cz.kudladev.vehicletracking.core.data.auth.models.RefreshRequest
import cz.kudladev.vehicletracking.core.data.auth.models.TokenPair
import cz.kudladev.vehicletracking.core.datastore.DataStoreRepository
import cz.kudladev.vehicletracking.core.domain.auth.UserStateHolder
import cz.kudladev.vehicletracking.network.AuthSessionManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class AuthSessionManagerImpl(
    private val dataStoreRepository: DataStoreRepository,
    private val userStateHolder: UserStateHolder
) : AuthSessionManager {

    override suspend fun loadTokens(): Pair<String?, String?> {
        return dataStoreRepository.getTokens()
    }

    override suspend fun refreshTokens(refreshToken: String, refreshClient: HttpClient): Pair<String, String>? {
        return try {
            val response = refreshClient.post("/auth/refresh") {
                contentType(ContentType.Application.Json)
                setBody(RefreshRequest(refreshToken))
            }
            if (response.status.isSuccess()) {
                val tokenPair = response.body<TokenPair>()
                dataStoreRepository.saveTokens(tokenPair.accessToken, tokenPair.refreshToken)
                tokenPair.accessToken to tokenPair.refreshToken
            } else {
                if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden) {
                    clearSession()
                }
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun clearSession() {
        dataStoreRepository.clearSession()
        userStateHolder.clearSession()
    }
}
