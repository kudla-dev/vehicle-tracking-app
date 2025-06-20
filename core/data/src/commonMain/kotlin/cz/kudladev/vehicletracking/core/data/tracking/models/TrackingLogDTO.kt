package cz.kudladev.vehicletracking.core.data.tracking.models

import cz.kudladev.vehicletracking.core.data.auth.models.UserResponse
import cz.kudladev.vehicletracking.core.data.auth.models.toDomain
import cz.kudladev.vehicletracking.model.TrackingLog
import cz.kudladev.vehicletracking.model.toTrackingState
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable
data class TrackingLogDTO(
        val trackingId: String,
        val state: String,
        val message: String? = null,
        val assignedBy: UserResponse,
        val assignedAt: String,
        val images: List<String>? = null,
)

fun TrackingLogDTO.toDomain(): TrackingLog {
    return TrackingLog(
        trackingId = trackingId,
        state = state.toTrackingState(),
        message = message,
        assignedBy = assignedBy.toDomain(),
        assignedAt = assignedAt.fromInstantToLocalDateTime(),
        images = images ?: emptyList()
    )
}

@OptIn(ExperimentalTime::class)
fun String.fromInstantToLocalDateTime(): LocalDateTime {
    return Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())
}