package cz.kudladev.vehicletracking.core.data.vehicles.models

import kotlinx.serialization.Serializable

@Serializable
data class VehicleImageRequest(
    val position: Int,
)
