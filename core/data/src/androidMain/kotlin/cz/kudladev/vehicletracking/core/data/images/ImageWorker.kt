package cz.kudladev.vehicletracking.core.data.images

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import cz.kudladev.vehicletracking.core.domain.vehicles.VehicleRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ImageWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val vehicleRepository: VehicleRepository by inject()

    override suspend fun doWork(): Result {
        val imagePath = inputData.getString("imagePath")
        val vehicleId = inputData.getLong("vehicleId", -1)
        val position = inputData.getInt("position", -1)

        if (imagePath == null || vehicleId == -1L) {
            return Result.failure(workDataOf("error" to "Missing image path or vehicle ID"))
        }

        try {
            // Read image data from the file
            val file = java.io.File(imagePath)
            if (!file.exists()) {
                return Result.failure(workDataOf("error" to "Image file not found: $imagePath"))
            }

            val imageData = file.readBytes()

            vehicleRepository.uploadImage(imageData, vehicleId, position).collect { result ->
                when (result) {
                    is cz.kudladev.vehicletracking.model.Result.Success -> {
                        val progressUpdate = result.data
                        val progress = workDataOf(
                            "progress" to (progressUpdate.byteSent.toFloat() / progressUpdate.totalBytes.toFloat()),
                        )
                        setProgress(progress)
                    }
                    is cz.kudladev.vehicletracking.model.Result.Error -> {
                        val error = workDataOf("error" to result.error.toString())
                        setProgress(error)
                    }
                    is cz.kudladev.vehicletracking.model.Result.Loading -> {
                        // Handle loading state if needed
                    }
                }
            }

            // Delete the temporary file after successful upload
            file.delete()

            return Result.success()
        } catch (e: Exception) {
            return Result.failure(workDataOf("error" to e.message))
        }
    }
}
