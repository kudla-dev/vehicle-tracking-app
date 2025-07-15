package cz.kudladev.vehicletracking.core.domain.tracking

import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.Result
import cz.kudladev.vehicletracking.model.Tracking
import cz.kudladev.vehicletracking.model.TrackingState
import kotlinx.datetime.LocalDateTime

interface TrackingRepository {

    suspend fun createTracking(
        vehicleId: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
    ): Result<Tracking, ErrorMessage>

    suspend fun getCurrentTracking(): Result<Tracking?, ErrorMessage>

    suspend fun getTrackings(
        states: List<TrackingState> = emptyList(),
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