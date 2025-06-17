package cz.kudladev.vehicletracking.core.data.auth.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val phoneNumber: String
)