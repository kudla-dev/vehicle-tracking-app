package cz.kudladev.vehicletracking.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
actual fun createDataStore(context: Any?): DataStore<Preferences> {
    try {
        require(
            value = context is AppContext,
            lazyMessage = { "Context object is required." }
        )
        val appContext = context.get() as Context

        return AppSetting.getDataStore(
            producePath = {
                appContext.filesDir
                    .resolve(dataStoreFileName)
                    .absolutePath
            }
        )
    } catch (e: Exception) {
        throw IllegalStateException("Failed to create DataStore", e)
    }
}