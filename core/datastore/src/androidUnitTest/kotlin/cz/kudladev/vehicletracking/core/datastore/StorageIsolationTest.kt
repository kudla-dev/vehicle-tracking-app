package cz.kudladev.vehicletracking.core.datastore

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import kotlin.test.*

class StorageIsolationTest {
    @Test fun realPreferenceFilesStaySeparateAndLegacyBytesAreUntouched() = runTest {
        val root = Files.createTempDirectory("motopark-isolation").toFile()
        val legacy = root.resolve(dataStoreFileName).apply { writeText("legacy evidence") }
        val a = StorageScope("dev", "https://a.example.invalid:443")
        val b = StorageScope("dev", "https://b.example.invalid:443")
        fun store(scope: StorageScope) = AppSetting.getDataStore {
            root.resolve(scope.directory).apply { mkdirs() }.resolve(dataStoreFileName).path
        }
        val token = stringPreferencesKey("access_token")
        val user = stringPreferencesKey("user")
        store(a).edit { it[token] = "session-a"; it[user] = "user-a" }
        assertNull(store(b).data.first()[token])
        assertNull(store(b).data.first()[user])
        store(b).edit { it[token] = "session-b" }
        assertEquals("session-a", store(a).data.first()[token])
        assertEquals("user-a", store(a).data.first()[user])
        assertEquals("session-b", store(b).data.first()[token])
        assertSame(store(a), store(a))
        assertEquals("legacy evidence", legacy.readText())
    }
}
