package cz.kudladev.vehicletracking.core.domain.tracking

import cz.kudladev.vehicletracking.core.domain.vehicles.ProgressUpdate
import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.Result
import cz.kudladev.vehicletracking.model.Tracking
import cz.kudladev.vehicletracking.model.TrackingState
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

interface TrackingRepository {

    suspend fun createTracking(
        vehicleId: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
    ): Result<Tracking, ErrorMessage>

    suspend fun updateTracking(
        trackingId: String,
        state: TrackingState,
        message: String? = null,
        tachometer: Int? = null,
    ): Result<Tracking, ErrorMessage>

    suspend fun getCountByState(
        state: TrackingState,
    ): Result<Int, ErrorMessage>

    suspend fun uploadImage(
        imageData: ByteArray,
        trackingId: String,
        position: Int,
        state: String
    ): Flow<Result<ProgressUpdate, ErrorMessage>>

    suspend fun getCurrentTracking(): Result<Tracking?, ErrorMessage>

    suspend fun getTrackings(
        states: List<TrackingState> = emptyList(),
        userId: String? = null,
        vehicleId: Long? = null,
        page: Int = 0,
        pageSize: Int = 10,
    ): Result<List<Tracking>, ErrorMessage>

    suspend fun getTracking(id: String): Result<Tracking, ErrorMessage>

    suspend fun getUserTrackingHistory(
        userId: String,
        page: Int = 0,
        pageSize: Int = 10,
        includeActive: Boolean = false,
    ): Result<List<Tracking>, ErrorMessage>

}