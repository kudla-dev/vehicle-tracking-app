package cz.kudladev.vehicletracking.auth.presentation.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.kudladev.vehicletracking.auth.domain.use_cases.EmailValidation
import cz.kudladev.vehicletracking.auth.presentation.login.LoginScreenAction
import cz.kudladev.vehicletracking.auth.presentation.login.LoginScreenState
import cz.kudladev.vehicletracking.core.domain.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginScreenViewModel(
    private val emailValidation: EmailValidation,
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
                validate()
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

        return hasError
    }


}