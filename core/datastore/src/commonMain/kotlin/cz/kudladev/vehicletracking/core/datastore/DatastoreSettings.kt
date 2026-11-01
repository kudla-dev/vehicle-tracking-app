package cz.kudladev.vehicletracking.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import okio.Path.Companion.toPath

internal const val dataStoreFileName = "vehicle_tracking.preferences_pb"

object AppSetting {
    private val stores = mutableMapOf<String, DataStore<Preferences>>()
    private val lock = SynchronizedObject()
    fun getDataStore(producePath: () -> String): DataStore<Preferences> = synchronized(lock) {
        val path = producePath()
        stores.getOrPut(path) { PreferenceDataStoreFactory.createWithPath(produceFile = { path.toPath() }) }
    }
}
