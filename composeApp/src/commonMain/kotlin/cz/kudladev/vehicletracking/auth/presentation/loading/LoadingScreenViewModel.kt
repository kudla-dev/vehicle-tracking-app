package cz.kudladev.vehicletracking.auth.presentation.loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.kudladev.vehicletracking.auth.domain.AuthRepository
import cz.kudladev.vehicletracking.auth.domain.UserStateHolder
import cz.kudladev.vehicletracking.datastore.DataStoreRepository
import cz.kudladev.vehicletracking.network.onError
import cz.kudladev.vehicletracking.network.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoadingScreenViewModel(
    private val authRepository: AuthRepository,
    private val userStateHolder: UserStateHolder,
    private val dataStoreRepository: DataStoreRepository
): ViewModel() {


    private val _state = MutableStateFlow(LoadingScreenState())
    val state = _state
        .onStart {
            auth()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = LoadingScreenState()
        )

    fun auth() = viewModelScope.launch {
        _state.update { it.copy(
            loadingProcess = LoadingProcess.Loading
        ) }
        authRepository
            .auth()
            .onSuccess { response ->
                println("Response: $response")
                userStateHolder.updateUser(response)
                _state.update { it.copy(
                    loadingProcess = LoadingProcess.Success
                ) }
            }
            .onError { error ->
                println("Error: $error")
                userStateHolder.updateUser(null)
                dataStoreRepository.clearTokens()
                _state.update { it.copy(
                    loadingProcess = LoadingProcess.Error(error.message)
                ) }
            }
    }

}