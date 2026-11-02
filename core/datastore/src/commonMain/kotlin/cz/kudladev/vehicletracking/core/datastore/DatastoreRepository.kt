package cz.kudladev.vehicletracking.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import cz.kudladev.vehicletracking.model.User
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class DataStoreRepository(
    context: AppContext,
    scope: StorageScope,
    private val secureTokenStorage: SecureTokenStorage = createSecureTokenStorage(context, scope),
    private val dataStore: DataStore<Preferences> = createDataStore(context, scope)
) {

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey(name = "access_token")
        val REFRESH_TOKEN = stringPreferencesKey(name = "refresh_token")
        val USER = stringPreferencesKey(name = "user")
    }

    suspend fun getAccessToken(): String? {
        val secure = secureTokenStorage.getAccessToken()
        if (secure != null) return secure

        val legacy = dataStore.data
            .catch { exception ->
                emit(emptyPreferences())
                exception.printStackTrace()
            }
            .map { preferences ->
                preferences[ACCESS_TOKEN]
            }
            .firstOrNull()

        if (legacy != null) {
            secureTokenStorage.saveAccessToken(legacy)
            dataStore.edit { it.remove(ACCESS_TOKEN) }
            return legacy
        }
        return null
    }

    suspend fun getRefreshToken(): String? {
        val secure = secureTokenStorage.getRefreshToken()
        if (secure != null) return secure

        val legacy = dataStore.data
            .catch { exception ->
                emit(emptyPreferences())
                exception.printStackTrace()
            }
            .map { preferences ->
                preferences[REFRESH_TOKEN]
            }
            .firstOrNull()

        if (legacy != null) {
            secureTokenStorage.saveRefreshToken(legacy)
            dataStore.edit { it.remove(REFRESH_TOKEN) }
            return legacy
        }
        return null
    }

    suspend fun getTokens(): Pair<String?, String?> {
        val (access, refresh) = secureTokenStorage.getTokens()
        if (access != null || refresh != null) {
            return access to refresh
        }

        val legacy = dataStore.data
            .catch { exception ->
                emit(emptyPreferences())
                exception.printStackTrace()
            }
            .map { preferences ->
                preferences[ACCESS_TOKEN] to preferences[REFRESH_TOKEN]
            }
            .firstOrNull() ?: (null to null)

        if (legacy.first != null || legacy.second != null) {
            legacy.first?.let { secureTokenStorage.saveAccessToken(it) }
            legacy.second?.let { secureTokenStorage.saveRefreshToken(it) }
            dataStore.edit { preferences ->
                preferences.remove(ACCESS_TOKEN)
                preferences.remove(REFRESH_TOKEN)
            }
            return legacy
        }
        return null to null
    }

    suspend fun saveAccessToken(token: String) {
        secureTokenStorage.saveAccessToken(token)
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
        }
    }

    suspend fun saveRefreshToken(token: String) {
        secureTokenStorage.saveRefreshToken(token)
        dataStore.edit { preferences ->
            preferences.remove(REFRESH_TOKEN)
        }
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        secureTokenStorage.saveTokens(accessToken, refreshToken)
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
    }

    suspend fun saveUser(user: User?) {
        dataStore.edit { preferences ->
            if (user == null) {
                preferences.remove(USER)
            } else {
                preferences[USER] = Json.encodeToString(user)
            }
        }
    }

    suspend fun getUser(): User? {
        return dataStore.data
            .catch { exception ->
                emit(emptyPreferences())
                exception.printStackTrace()
            }
            .map { preferences ->
                val userData = preferences[USER]
                if (userData.isNullOrBlank() || userData == "null") {
                    null
                } else {
                    try {
                        Json.decodeFromString<User>(userData)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }
            .firstOrNull()
    }

    suspend fun clearTokens() {
        secureTokenStorage.clearTokens()
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
    }

    suspend fun clearSession() {
        secureTokenStorage.clearTokens()
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
            preferences.remove(USER)
        }
    }
}
