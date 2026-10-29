package cz.kudladev.vehicletracking.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentationDirectory
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun createDataStore(context: Any?): DataStore<Preferences> {
    return AppSetting.getDataStore(producePath = {
        val manager = NSFileManager.defaultManager
        val legacy = manager.URLForDirectory(NSDocumentationDirectory, NSUserDomainMask, null, false, null)
            ?.path?.let { "$it/$dataStoreFileName" }
        if (legacy != null && manager.fileExistsAtPath(legacy)) {
            legacy
        } else {
            val directory = requireNotNull(manager.URLForDirectory(
                NSApplicationSupportDirectory, NSUserDomainMask, null, true, null
            )?.path)
            "$directory/$dataStoreFileName"
        }
    })
}
