package cz.kudladev.vehicletracking.auth.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val role: String,
    val maximumDistance: Int,
    val overallDistance: Int
)
