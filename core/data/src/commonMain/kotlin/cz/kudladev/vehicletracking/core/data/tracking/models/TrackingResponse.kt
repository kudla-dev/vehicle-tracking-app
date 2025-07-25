package cz.kudladev.vehicletracking.core.data.tracking.models

import cz.kudladev.vehicletracking.core.data.auth.models.UserResponse
import cz.kudladev.vehicletracking.core.data.auth.models.toDomain
import cz.kudladev.vehicletracking.core.data.vehicles.models.VehicleBasic
import cz.kudladev.vehicletracking.core.data.vehicles.models.toDomain
import cz.kudladev.vehicletracking.model.Tracking
import kotlinx.serialization.Serializable

@Serializable
data class TrackingResponse(
    val id: String,
    val vehicle: VehicleBasic,
    val user: UserResponse,
    val startTime: String,
    val endTime: String,
    val startTachometer: Int?,
    val endTachometer: Int?,
    val finalDistance: Int?,
    val stateLogs: List<TrackingLogDTO>,
)

fun TrackingResponse.toDomain(): Tracking {
    return Tracking(
        id = id,
        vehicle = vehicle.toDomain(),
        user = user.toDomain(),
        startTime = startTime.fromInstantToLocalDateTime(),
        endTime = endTime.fromInstantToLocalDateTime(),
        startTachometer = startTachometer,
        endTachometer = endTachometer,
        finalDistance = finalDistance,
        stateLogs = stateLogs.map { it.toDomain() }
    )
}