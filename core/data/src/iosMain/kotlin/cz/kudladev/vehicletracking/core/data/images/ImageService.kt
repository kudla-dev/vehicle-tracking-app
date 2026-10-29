package cz.kudladev.vehicletracking.core.data.images

import cz.kudladev.vehicletracking.core.data.images.upload.ImageUploadClient
import cz.kudladev.vehicletracking.core.data.images.upload.PersistentUploadQueue
import cz.kudladev.vehicletracking.core.data.images.upload.uploadError
import cz.kudladev.vehicletracking.core.data.images.upload.UploadRecord
import cz.kudladev.vehicletracking.model.ImageUploadState
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import platform.Foundation.NSUUID
import cz.kudladev.vehicletracking.core.data.images.upload.*

/** Application-lifetime transfers; records/photos survive suspension and relaunch. */
actual class ImageService(httpClient: HttpClient) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val client = ImageUploadClient(httpClient)
    private val pendingClear = MutableStateFlow<Job?>(null)
    private val store = IosUploadStore()
    private val queue = PersistentUploadQueue(store, scope, prepare = client::snapshotIds) { record, bytes ->
        when {
            record.sourceUrl != null -> client.vehicleUrl(record.sourceUrl, requireNotNull(record.vehicleId), record.position)
            record.vehicleId != null -> client.vehicle(requireNotNull(bytes), record.vehicleId, record.position)
            else -> client.tracking(requireNotNull(bytes), requireNotNull(record.trackingId), record.position,
                requireNotNull(record.trackingState))
        }
    }

    actual suspend fun enqueueBackgroundUpload(imageData: ByteArray, vehicleId: Long, position: Int) {
        enqueue(UploadRecord(id(), "vehicle_image_upload_$vehicleId", vehicleId = vehicleId, position = position), imageData)
    }
    actual suspend fun enqueueBackgroundUpload(imageData: String, vehicleId: Long, position: Int) {
        require(imageData.isNotBlank())
        enqueue(UploadRecord(id(), "vehicle_image_upload_$vehicleId", vehicleId = vehicleId, position = position, sourceUrl = imageData), null)
    }
    actual suspend fun enqueueBackgroundUpload(imageData: ByteArray, trackingId: String, position: Int, state: String) {
        enqueueBackgroundUpload(imageData, trackingId, position, state, 0)
    }
    actual suspend fun enqueueBackgroundUpload(imageData: ByteArray, trackingId: String, position: Int, state: String, temp: Int) {
        enqueue(UploadRecord(id(), "tracking_image_upload_${trackingId}_$state", trackingId = trackingId,
            trackingState = state, position = position), imageData)
    }
    actual fun cancelUpload(uploadId: String) {
        scope.launch(start = CoroutineStart.UNDISPATCHED) { queue.cancel(uploadId) }
    }
    actual fun clearUploadStatus() {
        pendingClear.value = scope.launch(start = CoroutineStart.UNDISPATCHED) { queue.dismissCompleted() }
    }

    private suspend fun enqueue(record: UploadRecord, bytes: ByteArray?) {
        pendingClear.value?.join()
        queue.enqueue(record, bytes)
    }

    actual fun observeUploads() = queue.snapshots.map { it.map { snapshot -> snapshot.task() } }
    actual suspend fun localUploadPhoto(id: String): ByteArray? {
        val record = queue.snapshots.value.find { it.record.id == id }?.record ?: return null
        return if (record.sourceUrl == null && record.state != UploadRecord.State.COMPLETED) store.readBytes(id) else null
    }
    actual suspend fun retryUpload(id: String) { queue.retry(id) }
    actual suspend fun reconcileUpload(id: String): Boolean = queue.reconcile(id, client::reconcile)
    actual fun getUploadStatus(): Flow<List<UploadStatus>> = queue.snapshots.map { it.map { s -> s.status() } }
    actual fun getUploadStatusByTag(tag: String): Flow<List<ImageUploadState>> = queue.snapshots.map { snapshots ->
        snapshots.filter { tag.isEmpty() || it.record.tag == tag || (tag == "image_upload" && it.record.vehicleId != null) }.sortedBy { it.record.position }.map { it.imageState() }
    }
    internal fun close() { scope.cancel() }

    private fun id() = NSUUID().UUIDString
}
