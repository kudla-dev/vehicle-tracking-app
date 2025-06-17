package cz.kudladev.vehicletracking.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorMessage(
    val error: String,
    val message: String,
    val path: String,
    val status: Int,
    val timestamp: String
)