package cz.kudladev.vehicletracking.core.domain.images

import cz.kudladev.vehicletracking.model.Image
import cz.kudladev.vehicletracking.model.ImageUploadState
import cz.kudladev.vehicletracking.model.TrackingState
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    suspend fun uploadImageToVehicle(image: Image, vehicleId: Long)
    suspend fun uploadImageToTracking(
        image: Image,
        trackingId: String,
        position: Int,
        state: TrackingState
    )
    fun cancelUpload(id: String)
    fun getUploadStatus(tag: String = ""): Flow<List<ImageUploadState>>
    fun clearUploadStatus()
}