package cz.kudladev.vehicletracking.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import vehicletracking.model.generated.resources.Res
import vehicletracking.model.generated.resources.admin
import vehicletracking.model.generated.resources.user

@Serializable
data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val role: Role,
    val maximumDistance: Int,
    val overallDistance: Int,
    val profilePicture: ImageWithUrl? = null
)

enum class Role(
    val value: String,
    val displayName: StringResource,
    val icon: ImageVector
) {
    USER(
        value = "USER",
        displayName = Res.string.user,
        icon = Icons.Default.Person
    ),
    ADMIN(
        value = "ADMIN",
        displayName = Res.string.admin,
        icon = Icons.Default.Shield
    )
}

fun String.toRole(): Role {
    return when (this.uppercase()) {
        Role.USER.value -> Role.USER
        Role.ADMIN.value -> Role.ADMIN
        else -> Role.USER
    }
}

fun User.isAdmin(): Boolean {
    return this.role == Role.ADMIN
}

