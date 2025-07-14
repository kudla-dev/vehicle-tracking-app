package cz.kudladev.vehicletracking.core.domain.tracking

import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.Result
import cz.kudladev.vehicletracking.model.Tracking
import kotlinx.datetime.LocalDateTime

interface TrackingRepository {

    suspend fun createTracking(
        vehicleId: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
    ): Result<Tracking, ErrorMessage>

    suspend fun getCurrentTracking(): Result<Tracking?, ErrorMessage>

    suspend fun getTrackings(
        states: List<String> = emptyList(),
        page: Int = 0,
        pageSize: Int = 10,
    ): Result<List<Tracking>, ErrorMessage>

}