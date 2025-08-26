package cz.kudladev.vehicletracking.core.data.images

import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.ImageUploadState
import kotlinx.coroutines.flow.Flow

expect class ImageService {

    /**
     * Enqueues an image for background upload, persisting across app restarts
     * @param imageData The image data to upload
     * @return ID of the background work
     */
    suspend fun enqueueBackgroundUpload(imageData: ByteArray, vehicleId: Long, position: Int)
    suspend fun enqueueBackgroundUpload(
        imageData: ByteArray,
        trackingId: String,
        position: Int,
        state: String
    )

    suspend fun enqueueBackgroundUpload(
        imageData: ByteArray,
        trackingId: String,
        position: Int,
        state: String,
        temp: Int = 0
    )

    suspend fun enqueueBackgroundUpload(
        imageData: String,
        vehicleId: Long,
        position: Int
    )

    /**
     * Cancels a pending or ongoing upload
     * @param uploadId ID of the upload to cancel
     */
    fun cancelUpload(uploadId: String)

    /**
     * Gets the current status of all pending/active uploads
     * @return Flow of upload statuses
     */
    fun getUploadStatus(): Flow<List<UploadStatus>>

    fun getUploadStatusByTag(tag: String): Flow<List<ImageUploadState>>


    fun clearUploadStatus()

}

data class UploadStatus(
    val id: String,
    val progress: Float, // 0.0f to 1.0f
    val state: UploadState,
    val error: ErrorMessage? = null,
    val imageData: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as UploadStatus

        if (id != other.id) return false
        if (progress != other.progress) return false
        if (state != other.state) return false
        if (error != other.error) return false
        if (imageData != null) {
            if (other.imageData == null) return false
            if (!imageData.contentEquals(other.imageData)) return false
        } else if (other.imageData != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + progress.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + (error?.hashCode() ?: 0)
        result = 31 * result + (imageData?.contentHashCode() ?: 0)
        return result
    }
}

enum class UploadState {
    QUEUED,
    UPLOADING,
    COMPLETED,
    FAILED,
    CANCELED
}
