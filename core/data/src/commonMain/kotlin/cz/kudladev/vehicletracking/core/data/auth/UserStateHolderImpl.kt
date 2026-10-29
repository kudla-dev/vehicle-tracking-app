package cz.kudladev.vehicletracking.core.data.auth

import cz.kudladev.vehicletracking.core.datastore.DataStoreRepository
import cz.kudladev.vehicletracking.core.domain.auth.UserStateHolder
import cz.kudladev.vehicletracking.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserStateHolderImpl(
    private val dataStoreRepository: DataStoreRepository
): UserStateHolder {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val user: StateFlow<User?>
        get() = _state.asStateFlow()

    private val _state = MutableStateFlow<User?>(null)

    init {
        scope.launch {
            _state.value = dataStoreRepository.getUser()
        }
    }

    override fun updateUser(user: User?) {
        _state.value = user
        scope.launch {
            dataStoreRepository.saveUser(user)
        }
    }

    override suspend fun updateTokens(accessToken: String, refreshToken: String) {
        dataStoreRepository.saveTokens(accessToken, refreshToken)
    }

    override suspend fun clearSession() {
        _state.value = null
        dataStoreRepository.clearSession()
    }
}
