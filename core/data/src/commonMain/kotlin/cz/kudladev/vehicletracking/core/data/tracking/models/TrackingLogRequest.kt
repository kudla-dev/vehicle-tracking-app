package cz.kudladev.vehicletracking.core.data.tracking.models

import kotlinx.serialization.Serializable

@Serializable
data class TrackingLogRequest(
    val state: String,
    val message: String? = null,
    val tachometer: Int? = null,
)
