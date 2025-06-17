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
    context: AppContext
) {

    private val dataStore: DataStore<Preferences> = createDataStore(context)

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey(name = "access_token")
        val REFRESH_TOKEN = stringPreferencesKey(name = "refresh_token")
        val USER = stringPreferencesKey(name = "user")
    }

    suspend fun getAccessToken(): String? {
        return dataStore.data
            .catch { exception ->
                emit(emptyPreferences())
                exception.printStackTrace()
            }
            .map { preferences ->
                preferences[ACCESS_TOKEN]
            }
            .firstOrNull()
    }

    suspend fun getRefreshToken(): String? {
        return dataStore.data
            .catch { exception ->
                emit(emptyPreferences())
                exception.printStackTrace()
            }
            .map { preferences ->
                preferences[REFRESH_TOKEN]
            }
            .firstOrNull()
    }


    suspend fun saveAccessToken(token: String) {
        println("Saving access token: $token")
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token
        }
        println("Access token saved")
    }

    suspend fun saveRefreshToken(token: String) {
        println("Saving refresh token: $token")
        dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN] = token
        }
        println("Refresh token saved")
    }

    suspend fun saveUser(user: User?) {
        val userData = Json.encodeToString(user)
        println("Saving user: $userData")
        dataStore.edit { preferences ->
            preferences[USER] = userData
        }
        println("User saved")
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
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
    }
}