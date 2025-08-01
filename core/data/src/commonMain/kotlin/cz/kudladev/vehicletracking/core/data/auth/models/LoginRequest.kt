package cz.kudladev.vehicletracking.core.data.auth.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)