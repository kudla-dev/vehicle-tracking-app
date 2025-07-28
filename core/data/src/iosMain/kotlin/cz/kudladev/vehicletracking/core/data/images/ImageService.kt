package cz.kudladev.vehicletracking.core.data.images

import kotlinx.coroutines.flow.Flow

actual class ImageService {
    actual suspend fun enqueueBackgroundUpload(imageData: ByteArray, vehicleId: Long, position: Int) {
    }

    actual suspend fun enqueueBackgroundUpload(imageData: String, vehicleId: Long, position: Int) {
    }

    actual fun cancelUpload(uploadId: String) {
    }

    actual fun getUploadStatus(): Flow<List<cz.kudladev.vehicletracking.core.data.images.UploadStatus>> {
        TODO("Not yet implemented")
    }

    actual fun clearUploadStatus() {
    }

    actual suspend fun enqueueBackgroundUpload(imageData: ByteArray, trackingId: String, state: String) {
    }


}