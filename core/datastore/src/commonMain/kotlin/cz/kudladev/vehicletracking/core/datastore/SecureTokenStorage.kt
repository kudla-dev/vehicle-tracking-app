package cz.kudladev.vehicletracking.core.datastore

interface SecureTokenStorage {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun getTokens(): Pair<String?, String?>
    suspend fun saveAccessToken(token: String)
    suspend fun saveRefreshToken(token: String)
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun clearTokens()
}

expect fun createSecureTokenStorage(context: Any?, scope: StorageScope): SecureTokenStorage
