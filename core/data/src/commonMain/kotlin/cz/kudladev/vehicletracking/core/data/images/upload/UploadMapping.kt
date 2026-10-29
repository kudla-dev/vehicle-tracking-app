package cz.kudladev.vehicletracking.core.data.images.upload

import cz.kudladev.vehicletracking.core.data.images.*
import cz.kudladev.vehicletracking.model.*

internal fun UploadSnapshot.task() = UploadTask(record.id, record.tag,
    record.vehicleId?.let { "Vozidlo $it" } ?: record.trackingId?.let { "Rezervace $it / ${record.trackingState}" } ?: "Neznámý cíl – ruční obnova",
    record.position, UploadTaskState.valueOf(record.state.name), progress, record.error, record.vehicleId != null || record.trackingId != null)

internal fun UploadSnapshot.status() = UploadStatus(record.id, progress,
    UploadState.valueOf(record.state.name), record.error)

internal fun UploadSnapshot.imageState(): ImageUploadState = when (record.state) {
    UploadRecord.State.QUEUED -> ImageUploadState.Queued
    UploadRecord.State.UPLOADING -> ImageUploadState.Uploading(null, progress)
    UploadRecord.State.COMPLETED -> ImageUploadState.Completed(requireNotNull(record.image))
    UploadRecord.State.FAILED -> ImageUploadState.Failed(record.error ?: uploadError(500, "Upload failed"))
    UploadRecord.State.CANCELED -> ImageUploadState.Canceled(record.id)
}
