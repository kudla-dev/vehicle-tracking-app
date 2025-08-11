package cz.kudladev.vehicletracking.model

import kotlinx.serialization.Serializable

@Serializable
data class DriverLicense(
    val type: String
)
