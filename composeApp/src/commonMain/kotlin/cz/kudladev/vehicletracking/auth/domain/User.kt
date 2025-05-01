package cz.kudladev.vehicletracking.auth.domain

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.ui.graphics.vector.ImageVector

data class User(
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val role: Role,
    val maximumDistance: Int,
    val overallDistance: Int
)

enum class Role(
    val value: String,
    val displayName: String,
    val icon: ImageVector
) {
    USER(
        value = "USER",
        displayName = "User",
        icon = Icons.Default.Person
    ),
    ADMIN(
        value = "ADMIN",
        displayName = "Admin",
        icon = Icons.Default.Shield
    )
}
