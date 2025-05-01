package cz.kudladev.vehicletracking.auth.domain

import kotlinx.coroutines.flow.StateFlow

interface UserStateHolder {

    val user: StateFlow<User?>

    fun updateUser(user: User?)

}