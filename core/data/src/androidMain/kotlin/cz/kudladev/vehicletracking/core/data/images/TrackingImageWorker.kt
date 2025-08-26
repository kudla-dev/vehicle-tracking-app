package cz.kudladev.vehicletracking.core.data.images

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import cz.kudladev.vehicletracking.core.domain.tracking.TrackingRepository
import cz.kudladev.vehicletracking.core.domain.vehicles.VehicleRepository
import cz.kudladev.vehicletracking.model.ImageWithUrl
import cz.kudladev.vehicletracking.model.onError
import cz.kudladev.vehicletracking.model.onSuccess
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

            var imageUrl: ImageWithUrl? = null

            trackingRepository.uploadImage(
                imageData = imageData,
                trackingId = vehicleId,
                position = position,
                state = state
            ).collect { result ->
                result
                    .onSuccess { progressUpdate ->
                        val progress = workDataOf(
                            "progress" to (progressUpdate.byteSent.toFloat() / progressUpdate.totalBytes.toFloat()),
                            "imageUri" to imagePath,
                            "position" to position
                        )
                        imageUrl = progressUpdate.imageURL
                        setProgress(progress)
                    }
                    .onError { error ->
                        val error = workDataOf(
                            "error" to error.toString(),
                            "imageUri" to imagePath,
                            "position" to position
                        )
                        setProgress(error)
                    }
            }
            // Delete the temporary file after successful upload
            file.delete()
            return Result.success(
                workDataOf(
                    "imageUrl" to imageUrl?.url,
                    "imageID" to imageUrl?.id,
                    "imagePosition" to position,
                    "imageUri" to imagePath
                )
            )
        } catch (e: Exception) {
            return Result.failure(workDataOf("error" to e.message))
        }
    }
}
