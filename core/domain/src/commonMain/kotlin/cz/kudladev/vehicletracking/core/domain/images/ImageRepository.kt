package cz.kudladev.vehicletracking.core.domain.images

import cz.kudladev.vehicletracking.model.Image
import cz.kudladev.vehicletracking.model.ImageUploadStatus
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    suspend fun uploadImageToVehicle(image: Image, vehicleId: Long)
    fun cancelUpload(id: String)
    fun getUploadStatus(): Flow<List<ImageUploadStatus>>
    fun clearUploadStatus()
}