package cz.kudladev.vehicletracking.core.data.images

import cz.kudladev.vehicletracking.core.data.images.upload.awaitUploadedImage
import kotlinx.coroutines.CancellationException
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import cz.kudladev.vehicletracking.core.domain.tracking.TrackingRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class TrackingImageWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val trackingRepository: TrackingRepository by inject()

    override suspend fun doWork(): Result {
        val imagePath = inputData.getString("imagePath")
        val vehicleId = inputData.getString("trackingId") ?: ""
        val state = inputData.getString("state") ?: "UNKNOWN"
        val position = inputData.getInt("position", 0)

        if (imagePath == null || vehicleId.isEmpty()) {
            return Result.failure(workDataOf("error" to "Missing image path or vehicle ID"))
        }

        try {
            val file = File(imagePath)
            if (!file.exists()) {
                return Result.failure(workDataOf("error" to "Image file not found: $imagePath"))
            }

            val imageData = file.readBytes()

            val result = trackingRepository.uploadImage(imageData, vehicleId, position, state)
                .awaitUploadedImage { update ->
                    if (update.totalBytes > 0) setProgress(workDataOf(
                        "progress" to (update.byteSent.toFloat() / update.totalBytes).coerceIn(0f, 1f),
                        "imageUri" to imagePath, "position" to position
                    ))
                }
            if (result is cz.kudladev.vehicletracking.model.Result.Error) {
                // An uncertain acknowledgement must not be posted again automatically.
                return Result.failure(workDataOf("error" to result.error.message, "imageUri" to imagePath))
            }
            val imageUrl = (result as cz.kudladev.vehicletracking.model.Result.Success).data
            // Delete the temporary file after successful upload
            file.delete()
            return Result.success(
                workDataOf(
                    "imageUrl" to imageUrl.url,
                    "imageID" to imageUrl.id,
                    "imagePosition" to position,
                    "imageUri" to imagePath
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Result.failure(workDataOf("error" to (e.message ?: "Unknown error")))
        }
    }
}
