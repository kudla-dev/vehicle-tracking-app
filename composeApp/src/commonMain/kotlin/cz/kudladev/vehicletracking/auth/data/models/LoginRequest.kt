package cz.kudladev.vehicletracking.auth.data.models

data class LoginRequest(
    val email: String,
    val password: String
)