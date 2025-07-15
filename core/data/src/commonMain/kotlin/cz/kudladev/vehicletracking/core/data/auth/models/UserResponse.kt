package cz.kudladev.vehicletracking.core.data.auth.models

import cz.kudladev.vehicletracking.model.User
import cz.kudladev.vehicletracking.model.toRole
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

fun UserResponse.toDomain(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phoneNumber = phoneNumber,
        fullName = "$firstName $lastName",
        role = role.toRole(),
        maximumDistance = maximumDistance,
        overallDistance = overallDistance
    )
}
