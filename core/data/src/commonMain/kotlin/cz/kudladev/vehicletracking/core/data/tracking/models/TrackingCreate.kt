package cz.kudladev.vehicletracking.core.data.tracking.models

import cz.kudladev.vehicletracking.model.Tracking
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable
data class TrackingCreate (
    val vehicleId: Long,
    val startTime: String,
    val endTime: String,
)
