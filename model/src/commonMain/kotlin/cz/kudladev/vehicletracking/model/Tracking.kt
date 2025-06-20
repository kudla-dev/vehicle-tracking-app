package cz.kudladev.vehicletracking.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Tracking(
        val id: String,
        val vehicle: Vehicle,
        val userId: String,
        val startTime: LocalDateTime,
        val endTime: LocalDateTime,
        val startTachometer: Int?,
        val endTachometer: Int?,
        val finalDistance: Int?,
        val stateLogs: List<TrackingLog>,
)