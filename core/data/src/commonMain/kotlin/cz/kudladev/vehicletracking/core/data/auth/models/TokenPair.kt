package cz.kudladev.vehicletracking.core.data.auth.models

import kotlinx.serialization.Serializable

@Serializable
data class TokenPair(
    val accessToken: String,
    val refreshToken: String
)
