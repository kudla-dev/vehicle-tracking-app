package cz.kudladev.vehicletracking.auth.presentation.register

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.kudladev.vehicletracking.auth.domain.AuthRepository
import cz.kudladev.vehicletracking.auth.domain.UserStateHolder
import cz.kudladev.vehicletracking.auth.domain.use_cases.EmailValidation
import cz.kudladev.vehicletracking.auth.domain.use_cases.FirstNameValidation
import cz.kudladev.vehicletracking.auth.domain.use_cases.LastNameValidation
import cz.kudladev.vehicletracking.auth.domain.use_cases.PasswordConfirmValidation
import cz.kudladev.vehicletracking.auth.domain.use_cases.PasswordValidation
import cz.kudladev.vehicletracking.auth.domain.use_cases.PhoneNumberValidation
import cz.kudladev.vehicletracking.auth.presentation.register.RegisterScreenAction
import cz.kudladev.vehicletracking.auth.presentation.register.RegisterScreenState
import cz.kudladev.vehicletracking.datastore.DataStoreRepository
import cz.kudladev.vehicletracking.network.onError
import cz.kudladev.vehicletracking.network.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterScreenViewModel(
    private val emailValidation: EmailValidation,
    private val firstNameValidation: FirstNameValidation,
    private val lastNameValidation: LastNameValidation,
    private val passwordValidation: PasswordValidation,
    private val phoneNumberValidation: PhoneNumberValidation,
    private val passwordConfirmValidation: PasswordConfirmValidation,
    private val authRepository: AuthRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val userStateHolder: UserStateHolder
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterScreenState())
    val state = _state.asStateFlow()

    fun onAction(action: RegisterScreenAction) {
        when (action) {
            is RegisterScreenAction.OnConfirmPasswordChanged -> {
                eraseErrors()
                _state.update { it.copy(
                    confirmPassword = action.confirmPassword
                ) }
            }
            is RegisterScreenAction.OnEmailChanged -> {
                eraseErrors()
                _state.update { it.copy(
                    email = action.email
                ) }
            }
            is RegisterScreenAction.OnFirstNameChanged -> {
                eraseErrors()
                _state.update { it.copy(
                    firstName = action.firstName
                ) }
            }
            is RegisterScreenAction.OnLastNameChanged -> {
                eraseErrors()
                _state.update { it.copy(
                    lastName = action.lastName
                ) }
            }
            is RegisterScreenAction.OnPasswordChanged -> {
                eraseErrors()
                _state.update { it.copy(
                    password = action.password
                ) }
            }
            is RegisterScreenAction.OnPhoneNumberChanged -> {
                eraseErrors()
                _state.update { it.copy(
                    phoneNumber = action.phoneNumber
                ) }
            }
            is RegisterScreenAction.OnPasswordVisibleChanged -> {
                _state.update { it.copy(
                    visiblePassword = action.visible
                ) }
            }
            RegisterScreenAction.OnRegister -> {
                if (validate()) {
                    register(
                        email = _state.value.email.trim(),
                        firstName = _state.value.firstName.trim(),
                        lastName = _state.value.lastName.trim(),
                        password = _state.value.password,
                        phoneNumber = _state.value.phoneNumber
                    )
                }
            }

        }
    }

    private fun validate(): Boolean {
        val emailResult = emailValidation.execute(_state.value.email)
        val firstNameResult = firstNameValidation.execute(_state.value.firstName)
        val lastNameResult = lastNameValidation.execute(_state.value.lastName)
        val passwordResult = passwordValidation.execute(_state.value.password)
        val passwordConfirmResult = passwordConfirmValidation.execute(_state.value.password, _state.value.confirmPassword)
        val phoneNumberResult = phoneNumberValidation.execute(_state.value.phoneNumber)

        val hasError = listOf(
            emailResult,
            firstNameResult,
            lastNameResult,
            passwordResult,
            phoneNumberResult,
            passwordConfirmResult
        ).any { !it.isSuccessful }

        if (hasError) {
            _state.update { it.copy(
                firstNameError = firstNameResult.errorMessage,
                lastNameError = lastNameResult.errorMessage,
                emailError = lastNameResult.errorMessage,
                passwordError = passwordResult.errorMessage,
                phoneNumberError = phoneNumberResult.errorMessage,
                confirmPasswordError = passwordConfirmResult.errorMessage
            ) }
        } else {
            eraseErrors()
        }

        return !hasError
    }

    private fun eraseErrors(){
        _state.update { it.copy(
            firstNameError = null,
            lastNameError = null,
            emailError = null,
            passwordError = null,
            phoneNumberError = null,
            confirmPasswordError = null
        ) }
    }

    private fun register(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        phoneNumber: String
    ) = viewModelScope.launch {
        _state.update { it.copy(
            registrationProcess = RegistrationProcess.Loading
        ) }
        authRepository
            .register(
                email = email,
                firstName = firstName,
                lastName = lastName,
                password = password,
                phoneNumber = phoneNumber
            )
            .onSuccess{ response ->
                authRepository
                    .login(
                        email = email,
                        password = password
                    )
                    .onSuccess { response ->
                        dataStoreRepository.saveAccessToken(response.accessToken)
                        dataStoreRepository.saveRefreshToken(response.refreshToken)
                        authRepository
                            .auth()
                            .onSuccess { auth ->
                                userStateHolder.updateUser(auth)
                                _state.update { it.copy(
                                    registrationProcess = RegistrationProcess.Success
                                ) }
                            }
                            .onError { error ->
                                _state.update { it.copy(
                                    registrationProcess = RegistrationProcess.Error(error)
                                ) }
                                println("Auth error after registration: $error")
                            }
                    }
                    .onError { error ->
                        _state.update { it.copy(
                            registrationProcess = RegistrationProcess.Error(error)
                        ) }
                        println("Login error after registration: $error")
                    }
            }
            .onError { error ->
                _state.update { it.copy(
                    registrationProcess = RegistrationProcess.Error(error)
                ) }
                println("Registration error: $error")
            }
    }

}