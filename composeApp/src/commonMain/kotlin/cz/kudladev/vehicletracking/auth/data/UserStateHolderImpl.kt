package cz.kudladev.vehicletracking.auth.data

import cz.kudladev.vehicletracking.auth.domain.User
import cz.kudladev.vehicletracking.auth.domain.UserStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserStateHolderImpl: UserStateHolder {

    override val user: StateFlow<User?>
        get() = _state.asStateFlow()

    private val _state = MutableStateFlow<User?>(null)

    override fun updateUser(user: User?) {
        _state.update { user }
    }

}