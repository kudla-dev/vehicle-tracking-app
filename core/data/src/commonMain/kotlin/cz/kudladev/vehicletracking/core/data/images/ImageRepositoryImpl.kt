package cz.kudladev.vehicletracking.core.data.images

import cz.kudladev.vehicletracking.core.domain.images.ImageRepository
import cz.kudladev.vehicletracking.model.Image
import cz.kudladev.vehicletracking.model.ImageUploadState
import cz.kudladev.vehicletracking.model.ImageUploadStatus
import cz.kudladev.vehicletracking.model.ImageWithBytes
import cz.kudladev.vehicletracking.model.ImageWithUrl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

    override fun cancelUpload(id: String) {
        imageService.cancelUpload(id)
    }

    override fun getUploadStatus(): Flow<List<ImageUploadStatus>> {
        return imageService.getUploadStatus().map { uploadStatuses ->
            uploadStatuses.map {
                ImageUploadStatus(
                    id = it.id,
                    progress = it.progress,
                    state = when (it.state) {
                        UploadState.UPLOADING -> ImageUploadState.UPLOADING
                        UploadState.COMPLETED -> ImageUploadState.COMPLETED
                        UploadState.FAILED -> ImageUploadState.FAILED
                        UploadState.QUEUED -> ImageUploadState.QUEUED
                        UploadState.CANCELED -> ImageUploadState.CANCELED
                    },
                    errorMessage = it.error
                )
            }
        }
    }

    override fun clearUploadStatus() {
        imageService.clearUploadStatus()
    }

}