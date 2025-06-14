package cz.kudladev.vehicletracking.core.data.models.vehicle

import kotlinx.serialization.Serializable

@Serializable
data class VehicleImageRequest(
    val position: Int,
)
