package cz.kudladev.vehicletracking.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

actual fun createDataStore(context: Any?, scope: StorageScope): DataStore<Preferences> {
    try {
        val appContext = if (context is AppContext) {
            runCatching { context.get() as? Context }.getOrNull()
        } else if (context is Context) {
            context
        } else {
            null
        }

        return if (appContext != null) {
            AppSetting.getDataStore(
                producePath = {
                    appContext.filesDir
                        .resolve(scope.directory).apply { check(isDirectory || mkdirs()) }
                        .resolve(dataStoreFileName)
                        .absolutePath
                }
            )
        } else {
            AppSetting.getDataStore(
                producePath = {
                    val tmpDir = System.getProperty("java.io.tmpdir") ?: "/tmp"
                    val root = File(tmpDir).resolve("motopark_datastore")
                    root.resolve(scope.directory).apply { check(isDirectory || mkdirs()) }
                        .resolve(dataStoreFileName)
                        .absolutePath
                }
            )
        }
    } catch (e: Exception) {
        throw IllegalStateException("Failed to create DataStore", e)
    }
}
