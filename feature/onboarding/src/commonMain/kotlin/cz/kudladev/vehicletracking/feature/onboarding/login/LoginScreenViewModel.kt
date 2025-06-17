package cz.kudladev.vehicletracking.feature.onboarding.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.kudladev.vehicletracking.core.domain.auth.AuthRepository
import cz.kudladev.vehicletracking.core.domain.auth.UserStateHolder
import cz.kudladev.vehicletracking.core.domain.auth.validations.EmailValidation
import cz.kudladev.vehicletracking.model.ValidationResult
import cz.kudladev.vehicletracking.model.onError
import cz.kudladev.vehicletracking.model.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginScreenViewModel(
    private val emailValidation: EmailValidation,
    private val authRepository: AuthRepository,
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
                auth()
            }
            .onError { error ->
                _state.update { it.copy(
                    loginProgress = LoginProgress.Error(error),
                ) }
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
            }
    }




}