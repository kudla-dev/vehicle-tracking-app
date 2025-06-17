package cz.kudladev.vehicletracking.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences


expect fun createDataStore(context: Any? = null): DataStore<Preferences>