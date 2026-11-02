package cz.kudladev.vehicletracking.core.datastore

import androidx.datastore.preferences.core.edit
import cz.kudladev.vehicletracking.model.Role
import cz.kudladev.vehicletracking.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import kotlin.test.*

class SecureTokenStorageTest {

    @Test
    fun tokensAreSavedRetrievedAndClearedSecurely() = runTest {
        val scope = StorageScope("dev", "https://api.example.com")
        val storage = createSecureTokenStorage(null, scope)

        // Initially empty
        val (initialAccess, initialRefresh) = storage.getTokens()
        assertNull(initialAccess)
        assertNull(initialRefresh)
        assertNull(storage.getAccessToken())
        assertNull(storage.getRefreshToken())

        // Save tokens
        storage.saveTokens("access-token-123", "refresh-token-456")
        val (access, refresh) = storage.getTokens()
        assertEquals("access-token-123", access)
        assertEquals("refresh-token-456", refresh)
        assertEquals("access-token-123", storage.getAccessToken())
        assertEquals("refresh-token-456", storage.getRefreshToken())

        // Update single token
        storage.saveAccessToken("access-token-updated")
        assertEquals("access-token-updated", storage.getAccessToken())
        assertEquals("refresh-token-456", storage.getRefreshToken())

        storage.saveRefreshToken("refresh-token-updated")
        assertEquals("access-token-updated", storage.getAccessToken())
        assertEquals("refresh-token-updated", storage.getRefreshToken())

        // Clear tokens
        storage.clearTokens()
        val (clearedAccess, clearedRefresh) = storage.getTokens()
        assertNull(clearedAccess)
        assertNull(clearedRefresh)
    }

    @Test
    fun tokenStorageIsIsolatedAcrossScopes() = runTest {
        val scopeA = StorageScope("dev", "https://a.motopark.cz")
        val scopeB = StorageScope("dev", "https://b.motopark.cz")
        val scopeStage = StorageScope("stage", "https://a.motopark.cz")

        val storageA = createSecureTokenStorage(null, scopeA)
        val storageB = createSecureTokenStorage(null, scopeB)
        val storageStage = createSecureTokenStorage(null, scopeStage)

        storageA.saveTokens("token-a", "refresh-a")
        storageB.saveTokens("token-b", "refresh-b")

        assertEquals("token-a", storageA.getAccessToken())
        assertEquals("token-b", storageB.getAccessToken())
        assertNull(storageStage.getAccessToken())

        storageA.clearTokens()
        assertNull(storageA.getAccessToken())
        assertEquals("token-b", storageB.getAccessToken())
    }

    @Test
    fun legacyDataStoreTokensAreMigratedAndPurgedFromPlainDataStore() = runTest {
        val root = Files.createTempDirectory("datastore-migration").toFile()
        val scope = StorageScope("dev", "https://migration.motopark.cz")
        val rawDataStore = AppSetting.getDataStore {
            root.resolve(scope.directory).apply { mkdirs() }.resolve(dataStoreFileName).path
        }

        // Simulate legacy unencrypted tokens directly written to DataStore
        rawDataStore.edit { prefs ->
            prefs[DataStoreRepository.ACCESS_TOKEN] = "legacy-access"
            prefs[DataStoreRepository.REFRESH_TOKEN] = "legacy-refresh"
        }

        val secureStorage = createSecureTokenStorage(null, scope)
        secureStorage.clearTokens()

        // Create repository pointing to this DataStore and secure storage
        val repo = DataStoreRepository(AppContext, scope, secureStorage, rawDataStore)

        // Reading tokens via repository should trigger transparent migration
        val tokens = repo.getTokens()
        assertEquals("legacy-access", tokens.first)
        assertEquals("legacy-refresh", tokens.second)

        // Verify tokens are now in secureStorage
        assertEquals("legacy-access", secureStorage.getAccessToken())
        assertEquals("legacy-refresh", secureStorage.getRefreshToken())

        // Verify raw DataStore preferences have had plain tokens removed
        val rawPrefs = rawDataStore.data.first()
        assertNull(rawPrefs[DataStoreRepository.ACCESS_TOKEN])
        assertNull(rawPrefs[DataStoreRepository.REFRESH_TOKEN])
    }

    @Test
    fun userAndSessionLifecycleWorksWithSecureStorage() = runTest {
        val root = Files.createTempDirectory("datastore-lifecycle").toFile()
        val scope = StorageScope("dev", "https://lifecycle.motopark.cz")
        val rawDataStore = AppSetting.getDataStore {
            root.resolve(scope.directory).apply { mkdirs() }.resolve(dataStoreFileName).path
        }
        val secureStorage = createSecureTokenStorage(null, scope)
        val repo = DataStoreRepository(AppContext, scope, secureStorage, rawDataStore)

        val user = User(
            id = "user-1",
            firstName = "Jan",
            lastName = "Novak",
            fullName = "Jan Novak",
            email = "jan@motopark.cz",
            phoneNumber = "+420123456789",
            role = Role.USER,
            maximumDistance = 500,
            overallDistance = 1200
        )

        repo.saveUser(user)
        repo.saveTokens("access-live", "refresh-live")

        assertEquals(user, repo.getUser())
        assertEquals("access-live", repo.getAccessToken())
        assertEquals("refresh-live", repo.getRefreshToken())

        // Clear session must clear both user and secure tokens
        repo.clearSession()
        assertNull(repo.getUser())
        assertNull(repo.getAccessToken())
        assertNull(repo.getRefreshToken())
        assertNull(secureStorage.getAccessToken())
    }
}
