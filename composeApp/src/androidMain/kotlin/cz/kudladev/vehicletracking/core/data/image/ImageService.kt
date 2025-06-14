package cz.kudladev.vehicletracking.core.data.image

import android.content.Context
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import cz.kudladev.vehicletracking.core.domain.VehicleRepository
import cz.kudladev.vehicletracking.network.ErrorMessage
import cz.kudladev.vehicletracking.network.onError
import cz.kudladev.vehicletracking.network.onSuccess
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import java.util.UUID
import kotlin.compareTo
import kotlin.onSuccess
import kotlin.text.set
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.toString

actual class ImageService(
    private val context: Context,
    private val vehicleRepository: VehicleRepository
) {

    private val directUploadStatuses = MutableStateFlow<List<UploadStatus>>(emptyList())

    /**
     * Enqueues an image for background upload, persisting across app restarts
     * @param imageData The image data to upload
     * @return ID of the background work
     */
    actual suspend fun enqueueBackgroundUpload(imageData: ByteArray, vehicleId: Long, position: Int) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Save image data to a temporary file
        val fileName = "temp_image_${System.currentTimeMillis()}.jpg"
        val file = context.getFileStreamPath(fileName)

        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { outputStream ->
            outputStream.write(imageData)
        }

        val workRequest = OneTimeWorkRequestBuilder<ImageWorker>()
            .setConstraints(constraints)
            .setInputData(
                workDataOf(
                    "imagePath" to file.absolutePath,
                    "vehicleId" to vehicleId,
                    "position" to position,
                )
            )
            .addTag("image_upload")
            .build()

        WorkManager
            .getInstance(context)
            .enqueue(workRequest)
    }

    @OptIn(ExperimentalTime::class)
    actual suspend fun enqueueBackgroundUpload(imageData: String, vehicleId: Long, position: Int) {
        val id = "direct_upload_${System.currentTimeMillis()}"
        // Add QUEUED status
        updateDirectStatus(id, UploadState.QUEUED)
        vehicleRepository
            .uploadImage(
                imageUrl = imageData,
                vehicleId = vehicleId,
                position = position
            )
            .onSuccess {
                updateDirectStatus(id, UploadState.COMPLETED)
            }
            .onError { error ->
                updateDirectStatus(
                    id,
                    UploadState.FAILED,
                    ErrorMessage(
                        error = "Upload image failed",
                        message = error.message ?: "",
                        path = "",
                        status = 404,
                        timestamp = Clock.System.now().toString()
                    )
                )
            }
    }

    private fun updateDirectStatus(id: String, state: UploadState, error: ErrorMessage? = null) {
        val current = directUploadStatuses.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        val status = UploadStatus(id, progress = if (state == UploadState.COMPLETED) 1f else 0f, state, error)
        if (index >= 0) current[index] = status else current.add(status)
        directUploadStatuses.value = current
    }

    /**
     * Cancels a pending or ongoing upload
     * @param uploadId ID of the upload to cancel
     */
    actual fun cancelUpload(uploadId: String) {
        val uuid = UUID.fromString(uploadId)
        WorkManager
            .getInstance(context)
            .cancelWorkById(uuid)
    }

    /**
     * Gets the current status of all pending/active uploads
     * @return Flow of upload statuses
     */
    @OptIn(ExperimentalTime::class)
    actual fun getUploadStatus(): Flow<List<UploadStatus>> {
        val workflow = callbackFlow {
            val workInfoLiveData = WorkManager
                .getInstance(context)
                .getWorkInfosByTagLiveData("image_upload")

            val observer = Observer<List<WorkInfo>> { workInfos ->
                val uploadStatuses = workInfos.mapNotNull { workInfo ->
                    val id = workInfo.id.toString()
                    val progress = workInfo.progress.getFloat("progress", 0f)

                    val state = when (workInfo.state) {
                        WorkInfo.State.ENQUEUED -> UploadState.QUEUED
                        WorkInfo.State.RUNNING -> UploadState.UPLOADING
                        WorkInfo.State.SUCCEEDED -> UploadState.COMPLETED
                        WorkInfo.State.FAILED -> UploadState.FAILED
                        WorkInfo.State.CANCELLED -> UploadState.CANCELED
                        else -> return@mapNotNull null
                    }

                    val error = workInfo.outputData.getString("error")

                    UploadStatus(
                        id = id,
                        progress = progress,
                        state = state,
                        error = error?.let {
                            ErrorMessage(
                                error = "Upload image failed",
                                message = it,
                                path = "",
                                status = 404,
                                timestamp = Clock.System.now().toString()
                            )
                        }
                    )
                }

                trySend(uploadStatuses)
            }

            workInfoLiveData.observeForever(observer)

            awaitClose {
                workInfoLiveData.removeObserver(observer)
            }
        }
        return directUploadStatuses.combine(workflow) { directStatuses, workStatuses ->
            (directStatuses + workStatuses).sortedBy { it.id }
        }
    }


    actual fun clearUploadStatus() {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag("image_upload")
        workManager.pruneWork()
        directUploadStatuses.value = emptyList()
    }



}
