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
    fun observeUploads(): Flow<List<cz.kudladev.vehicletracking.model.UploadTask>>
    /** Explicit user decision after reviewing the server; never an automatic retry. */
    suspend fun localUploadPhoto(id: String): ByteArray?
    suspend fun retryUpload(id: String)
    /** GET-only reconciliation. True only for an exact, unique byte match. */
    suspend fun reconcileUpload(id: String): Boolean

    fun cancelUpload(id: String)
    fun getUploadStatus(tag: String = ""): Flow<List<ImageUploadState>>
    fun clearUploadStatus()
}