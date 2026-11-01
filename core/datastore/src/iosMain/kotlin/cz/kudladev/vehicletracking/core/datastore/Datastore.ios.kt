package cz.kudladev.vehicletracking.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun createDataStore(context: Any?, scope: StorageScope): DataStore<Preferences> =
    AppSetting.getDataStore {
        val manager = NSFileManager.defaultManager
        val root = requireNotNull(manager.URLForDirectory(
            NSApplicationSupportDirectory, NSUserDomainMask, null, true, null
        )?.path)
        val directory = "$root/${scope.directory}"
        check(manager.createDirectoryAtPath(directory, true, null, null))
        "$directory/$dataStoreFileName"
    }
