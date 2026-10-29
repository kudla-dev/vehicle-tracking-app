package cz.kudladev.vehicletracking.network

import io.ktor.client.HttpClient

interface AuthSessionManager {
    suspend fun loadTokens(): Pair<String?, String?>
    suspend fun refreshTokens(refreshToken: String, refreshClient: HttpClient): Pair<String, String>?
    suspend fun clearSession()
}
