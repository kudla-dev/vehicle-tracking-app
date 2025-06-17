package cz.kudladev.vehicletracking.core.domain.auth

import cz.kudladev.vehicletracking.model.User
import kotlinx.coroutines.flow.StateFlow

interface UserStateHolder {

    val user: StateFlow<User?>

    fun updateUser(user: User?)

    suspend fun updateTokens(
        accessToken: String,
        refreshToken: String
    )

}