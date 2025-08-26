package cz.kudladev.vehicletracking.core.data.images

import cz.kudladev.vehicletracking.core.domain.images.ImageRepository
import cz.kudladev.vehicletracking.model.*
import kotlinx.coroutines.flow.Flow

class ImageRepositoryImpl(
    private val imageService: ImageService
): ImageRepository {
    override suspend fun uploadImageToVehicle(image: Image, vehicleId: Long) {
        when (image) {
            is ImageWithBytes -> {
                imageService.enqueueBackgroundUpload(
                    imageData = image.bytes ?: ByteArray(0),
                    vehicleId = vehicleId,
                    position = image.position ?: 0
                )
            }
            is ImageWithUrl -> {
                imageService.enqueueBackgroundUpload(
                    imageData = image.url ?: "",
                    vehicleId = vehicleId,
                    position = image.position ?: 0
                )
            }
        }
    }

    override suspend fun uploadImageToTracking(
        image: Image,
        trackingId: String,
        position: Int,
        state: TrackingState
    ) {
        when (image) {
            is ImageWithBytes -> {
                imageService.enqueueBackgroundUpload(
                    imageData = image.bytes ?: ByteArray(0),
                    trackingId = trackingId,
                    position = position,
                    state = state.state,
                    temp = 0
                )
            }
        }
    }

    override fun cancelUpload(id: String) {
        imageService.cancelUpload(id)
    }

    override fun getUploadStatus(tag: String): Flow<List<ImageUploadState>> {
        return imageService.getUploadStatusByTag(tag)
    }

    override fun clearUploadStatus() {
        imageService.clearUploadStatus()
    }

}