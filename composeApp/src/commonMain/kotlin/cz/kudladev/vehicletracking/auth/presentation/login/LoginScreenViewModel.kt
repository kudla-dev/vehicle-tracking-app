package cz.kudladev.vehicletracking.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.kudladev.vehicletracking.auth.domain.AuthRepository
import cz.kudladev.vehicletracking.auth.domain.UserStateHolder
import cz.kudladev.vehicletracking.auth.domain.use_cases.EmailValidation
import cz.kudladev.vehicletracking.core.domain.use_cases.ValidationResult
import cz.kudladev.vehicletracking.datastore.DataStoreRepository
import cz.kudladev.vehicletracking.network.onError
import cz.kudladev.vehicletracking.network.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginScreenViewModel(
    private val emailValidation: EmailValidation,
    private val authRepository: AuthRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val userStateHolder: UserStateHolder
) : ViewModel() {

    private val _state = MutableStateFlow(LoginScreenState())
    val state = _state.asStateFlow()

    fun onAction(action: LoginScreenAction) {
        when (action) {
            is LoginScreenAction.OnEmailChanged -> {
                clearErrors()
                _state.update { it.copy(
                    email = action.email,
                ) }
            }
            is LoginScreenAction.OnPasswordChanged -> {
                clearErrors()
                _state.update { it.copy(
                    password = action.password,
                ) }
            }
            is LoginScreenAction.OnPasswordVisibleChanged -> {
                _state.update { it.copy(
                    visiblePassword = action.visible,
                ) }
            }
            LoginScreenAction.OnLogin -> {
                if (validate()) {
                    login(
                        email = _state.value.email,
                        password = _state.value.password,
                    )
                }
            }
        }
    }

    private fun clearErrors() {
        _state.update { it.copy(
            emailError = null,
            passwordError = null,
        ) }
    }

    private fun validate(): Boolean {
        val emailResult = emailValidation.execute(_state.value.email)
        val passwordResult = if (_state.value.password.isBlank()) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Password can't be blank"
            )
        } else {
            ValidationResult(
                isSuccessful = true
            )
        }

        val hasError = listOf(
            emailResult,
            passwordResult,
        ).any { !it.isSuccessful }


        if (hasError) {
            _state.update { it.copy(
                emailError = emailResult.errorMessage,
                passwordError = passwordResult.errorMessage,
            ) }
        } else {
            clearErrors()
        }

        return !hasError
    }

    private fun login(
        email: String,
        password: String
    ) = viewModelScope.launch {
        _state.update { it.copy(
            loginProgress = LoginProgress.Loading,
        ) }

        authRepository
            .login(
                email = email,
                password = password,
            )
            .onSuccess { response ->
                _state.update { it.copy(
                    loginProgress = LoginProgress.Success,
                ) }
                println("Saving in viewmodel")
                dataStoreRepository.saveAccessToken(response.accessToken)
                dataStoreRepository.saveRefreshToken(response.refreshToken)
                println("Saved in viewmodel")
                println("Starting auth")
                auth()
            }
            .onError { error ->
                _state.update { it.copy(
                    loginProgress = LoginProgress.Error(error),
                ) }
                dataStoreRepository.clearTokens()
            }
    }

    private fun auth() = viewModelScope.launch {
        _state.update { it.copy(
            loginProgress = LoginProgress.Loading
        ) }

        authRepository
            .auth()
            .onSuccess { user ->
                _state.update { it.copy(
                    loginProgress = LoginProgress.LoggedIn,
                ) }
                userStateHolder.updateUser(user)
            }
            .onError { error ->
                _state.update { it.copy(
                    loginProgress = LoginProgress.Error(error),
                ) }
                userStateHolder.updateUser(null)
                dataStoreRepository.clearTokens()
            }
    }




}