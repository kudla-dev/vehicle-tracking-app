package cz.kudladev.vehicletracking.auth.data.models

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequest(
    val refreshToken: String
)
