package cz.kudladev.vehicletracking.feature.onboarding.loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.kudladev.vehicletracking.core.domain.SnackbarController
import cz.kudladev.vehicletracking.core.domain.auth.AuthRepository
import cz.kudladev.vehicletracking.core.domain.auth.UserStateHolder
import cz.kudladev.vehicletracking.model.Snackbar
import cz.kudladev.vehicletracking.model.onError
import cz.kudladev.vehicletracking.model.onSuccess
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoadingScreenViewModel(
    private val authRepository: AuthRepository,
    private val userStateHolder: UserStateHolder,
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
                _state.update { it.copy(
                    loadingProcess = LoadingProcess.Error(error.message)
                ) }
            }
    }

}