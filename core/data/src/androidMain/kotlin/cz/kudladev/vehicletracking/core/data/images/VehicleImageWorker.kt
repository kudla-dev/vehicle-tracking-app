package cz.kudladev.vehicletracking.core.data.images

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

/** Compatibility entry point for pre-journal jobs. Photos are imported by AndroidUploadStore. */
class VehicleImageWorker(context: Context, parameters: WorkerParameters) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result = Result.failure(workDataOf(
        "error" to "Legacy upload interrupted; local photo retained for review"
    ))
}
