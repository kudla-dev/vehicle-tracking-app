package cz.kudladev.vehicletracking.model

enum class UploadTaskState { QUEUED, UPLOADING, COMPLETED, FAILED, CANCELED }

data class UploadTask(
    val id: String,
    val tag: String,
    val target: String,
    val position: Int,
    val state: UploadTaskState,
    val progress: Float,
    val error: ErrorMessage?,
    val targetKnown: Boolean = true,
)
