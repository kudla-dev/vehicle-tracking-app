package cz.kudladev.vehicletracking.core.data.auth.models

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequest(
    val refreshToken: String
)
