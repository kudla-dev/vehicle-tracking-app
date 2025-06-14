package cz.kudladev.vehicletracking.core.data.image

import kotlinx.coroutines.flow.Flow

actual class ImageService {

    /**
     * Enqueues an image for background upload, persisting across app restarts
     * @param imageData The image data to upload
     * @return ID of the background work
     */
    actual fun enqueueBackgroundUpload(imageData: ByteArray, vehicleId: Long): String {
        TODO()
    }

    /**
     * Cancels a pending or ongoing upload
     * @param uploadId ID of the upload to cancel
     */
    actual fun cancelUpload(uploadId: String) {
        TODO()

    }

    /**
     * Gets the current status of all pending/active uploads
     * @return Flow of upload statuses
     */
    actual fun getUploadStatus(): Flow<List<UploadStatus>> {
        TODO()

    }

    actual fun clearUploadStatus() {
        TODO()
    }

}