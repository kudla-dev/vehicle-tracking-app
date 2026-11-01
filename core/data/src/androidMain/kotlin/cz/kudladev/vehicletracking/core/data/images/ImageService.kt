package cz.kudladev.vehicletracking.core.data.images

import android.content.Context
import androidx.work.*
import cz.kudladev.vehicletracking.core.data.images.upload.*
import cz.kudladev.vehicletracking.model.ImageUploadState
import cz.kudladev.vehicletracking.core.datastore.StorageScope
import io.ktor.client.HttpClient
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID

actual class ImageService(context: Context, httpClient: HttpClient, private val storageScope: StorageScope) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val manager = WorkManager.getInstance(context)
    private val store = AndroidUploadStore(context, storageScope)
    private val client = ImageUploadClient(httpClient)
    private val queue = PersistentUploadQueue(store, scope, schedule = { id ->
        manager.enqueueUniqueWork("${storageScope.key}-upload-$id", ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<DurableImageWorker>()
                .setConstraints(Constraints.Builder().setRequiredNetworkType(if (storageScope.environment == "mock") NetworkType.NOT_REQUIRED else NetworkType.CONNECTED).build())
                .setInputData(workDataOf("uploadId" to id, "storageScope" to storageScope.key)).addTag("image_upload").build())
    }, prepare = client::snapshotIds) { record, bytes ->
        when {
            record.sourceUrl != null -> client.vehicleUrl(record.sourceUrl, requireNotNull(record.vehicleId), record.position, record.id)
            record.vehicleId != null -> client.vehicle(requireNotNull(bytes), record.vehicleId, record.position, record.id)
            else -> client.tracking(requireNotNull(bytes), requireNotNull(record.trackingId), record.position, requireNotNull(record.trackingState), record.id)
        }
    }
    private var pendingClear: Job? = null
    private suspend fun enqueue(record: UploadRecord, bytes: ByteArray?) {
        pendingClear?.join()
        queue.enqueue(record, bytes)
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
        manager.cancelUniqueWork("${storageScope.key}-upload-$uploadId")
        scope.launch(start = CoroutineStart.UNDISPATCHED) { queue.cancel(uploadId) }
    }
    actual fun clearUploadStatus() {
        // Screen cleanup only dismisses confirmed successes; it must not cancel other screens' work.
        pendingClear = scope.launch(start = CoroutineStart.UNDISPATCHED) { queue.dismissCompleted() }
    }
    actual fun observeUploads() = queue.snapshots.map { it.map { s -> s.task() } }
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
    internal suspend fun process(id: String, workerScope: String?) {
        // WorkManager survives APK updates. Old/foreign work must never use the current client.
        if (storageScope.acceptsWorker(workerScope)) queue.process(id)
    }
    internal fun close() { scope.cancel() }
    private fun id() = UUID.randomUUID().toString()
}

class DurableImageWorker(context: Context, parameters: WorkerParameters) : CoroutineWorker(context, parameters),
    org.koin.core.component.KoinComponent {
    override suspend fun doWork(): Result {
        val id = inputData.getString("uploadId") ?: return Result.failure()
        val workerScope = inputData.getString("storageScope")
        if (!getKoin().get<StorageScope>().acceptsWorker(workerScope)) return Result.success()
        val service = getKoin().get<ImageService>()
        service.process(id, workerScope)
        // Business failures are durable in the journal; never ask WorkManager to replay a POST.
        return Result.success()
    }
}
