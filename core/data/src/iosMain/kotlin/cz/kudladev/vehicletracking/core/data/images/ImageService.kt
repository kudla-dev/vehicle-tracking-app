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

/** Application-lifetime transfers; records/photos survive suspension and relaunch. */
actual class ImageService(httpClient: HttpClient) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val client = ImageUploadClient(httpClient)
    private val pendingClear = MutableStateFlow<Job?>(null)
    private val queue = PersistentUploadQueue(IosUploadStore(), scope) { record, bytes ->
        when {
            record.sourceUrl != null -> client.vehicleUrl(record.sourceUrl, requireNotNull(record.vehicleId), record.position)
            record.vehicleId != null -> client.vehicle(requireNotNull(bytes), record.vehicleId, record.position)
            else -> client.tracking(requireNotNull(bytes), requireNotNull(record.trackingId), record.position,
                requireNotNull(record.trackingState))
        }
    }

    actual suspend fun enqueueBackgroundUpload(imageData: ByteArray, vehicleId: Long, position: Int) {
        enqueue(UploadRecord(id(), "image_upload", vehicleId = vehicleId, position = position), imageData)
    }
    actual suspend fun enqueueBackgroundUpload(imageData: String, vehicleId: Long, position: Int) {
        require(imageData.isNotBlank())
        enqueue(UploadRecord(id(), "image_upload", vehicleId = vehicleId, position = position, sourceUrl = imageData), null)
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
        pendingClear.value = scope.launch(start = CoroutineStart.UNDISPATCHED) { queue.clear() }
    }

    private suspend fun enqueue(record: UploadRecord, bytes: ByteArray?) {
        pendingClear.value?.join()
        queue.enqueue(record, bytes)
    }

    actual fun getUploadStatus(): Flow<List<UploadStatus>> = queue.snapshots.map { snapshots ->
        snapshots.map { snapshot -> UploadStatus(snapshot.record.id, snapshot.progress, when (snapshot.record.state) {
            UploadRecord.State.QUEUED -> UploadState.QUEUED
            UploadRecord.State.UPLOADING -> UploadState.UPLOADING
            UploadRecord.State.COMPLETED -> UploadState.COMPLETED
            UploadRecord.State.FAILED -> UploadState.FAILED
            UploadRecord.State.CANCELED -> UploadState.CANCELED
        }, snapshot.record.error) }
    }
    actual fun getUploadStatusByTag(tag: String): Flow<List<ImageUploadState>> = queue.snapshots.map { snapshots ->
        snapshots.filter { tag.isEmpty() || it.record.tag == tag }.sortedBy { it.record.position }.map { snapshot ->
            val record = snapshot.record
            when (record.state) {
                UploadRecord.State.QUEUED -> ImageUploadState.Queued
                UploadRecord.State.UPLOADING -> ImageUploadState.Uploading(null, snapshot.progress)
                UploadRecord.State.COMPLETED -> ImageUploadState.Completed(requireNotNull(record.image))
                UploadRecord.State.FAILED -> ImageUploadState.Failed(record.error ?: uploadError(500, "Upload failed"))
                UploadRecord.State.CANCELED -> ImageUploadState.Canceled(record.id)
            }
        }
    }
    internal fun close() { scope.cancel() }

    private fun id() = NSUUID().UUIDString
}
