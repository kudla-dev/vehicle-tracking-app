package cz.kudladev.vehicletracking.auth.data.models

data class TokenPair(
    val accessToken: String,
    val refreshToken: String
)
