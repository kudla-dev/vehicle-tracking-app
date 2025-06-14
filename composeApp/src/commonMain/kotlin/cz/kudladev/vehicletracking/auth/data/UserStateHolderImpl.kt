package cz.kudladev.vehicletracking.auth.data

import cz.kudladev.vehicletracking.auth.domain.User
import cz.kudladev.vehicletracking.auth.domain.UserStateHolder
import cz.kudladev.vehicletracking.datastore.DataStoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserStateHolderImpl(
    private val dataStoreRepository: DataStoreRepository
): UserStateHolder {

    override val user: StateFlow<User?>
        get() = _state.asStateFlow()

    private val _state = MutableStateFlow<User?>(null)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _state.update { dataStoreRepository.getUser() }
        }
    }

    override fun updateUser(user: User?) {
        _state.update { user }
        CoroutineScope(Dispatchers.IO).launch {
            dataStoreRepository.saveUser(user)
        }
    }

}