package cz.kudladev.vehicletracking.auth.data.models

data class RegisterRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val phoneNumber: String
)