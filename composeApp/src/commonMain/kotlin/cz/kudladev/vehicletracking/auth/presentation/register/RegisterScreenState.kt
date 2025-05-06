package cz.kudladev.vehicletracking.auth.presentation.register

import cz.kudladev.vehicletracking.network.ErrorMessage

data class RegisterScreenState(
    val firstName: String = "",
    val firstNameError: String? = null,
    val lastName: String = "",
    val lastNameError: String? = null,
    val phoneNumber: String = "",
    val phoneNumberError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val visiblePassword: Boolean = false,
    val registrationProcess: RegistrationProcess = RegistrationProcess.Idle,
)

sealed class RegistrationProcess {
    object Idle : RegistrationProcess()
    object Loading : RegistrationProcess()
    object Success : RegistrationProcess()
    data class Error(val message: ErrorMessage) : RegistrationProcess()
}