package cz.kudladev.vehicletracking.core.data.models.vehicle

import kotlinx.serialization.Serializable

@Serializable
data class VehicleFromUrl(
    val fullName: String,
    val driverLicense: List<String>,
    val imageUrls: List<String>,
)