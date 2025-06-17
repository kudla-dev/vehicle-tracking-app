package cz.kudladev.vehicletracking.core.data.auth

import cz.kudladev.vehicletracking.core.datastore.DataStoreRepository
import cz.kudladev.vehicletracking.core.domain.auth.UserStateHolder
import cz.kudladev.vehicletracking.model.User
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

    override suspend fun updateTokens(accessToken: String, refreshToken: String) {
        dataStoreRepository.saveAccessToken(accessToken)
        dataStoreRepository.saveRefreshToken(refreshToken)
    }

}