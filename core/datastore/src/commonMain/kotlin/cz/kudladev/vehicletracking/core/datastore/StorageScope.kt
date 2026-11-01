package cz.kudladev.vehicletracking.core.datastore

import okio.ByteString.Companion.encodeUtf8

/** Versioned, collision-resistant namespace. Unscoped legacy data is never adopted. */
class StorageScope(environment: String, canonicalOrigin: String) {
    init {
        require(environment in setOf("mock", "dev", "stage", "prod"))
        require(canonicalOrigin.isNotBlank())
    }
    val key = "v1-$environment-" + canonicalOrigin.encodeUtf8().sha256().hex()
    val directory = "environments/$key"
    fun acceptsWorker(workerScope: String?): Boolean = workerScope == key
}
