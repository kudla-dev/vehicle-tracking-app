package cz.kudladev.vehicletracking.feature.onboarding.login

import cz.kudladev.vehicletracking.model.ErrorMessage

data class LoginScreenState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val visiblePassword: Boolean = false,
    val loginProgress: LoginProgress = LoginProgress.Idle,
)

sealed class LoginProgress {
    data object Idle : LoginProgress()
    data object Loading : LoginProgress()
    data object Success : LoginProgress()
    data object LoggedIn : LoginProgress()
    data class Error(val message: ErrorMessage) : LoginProgress()
}