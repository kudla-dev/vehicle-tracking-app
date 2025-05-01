package cz.kudladev.vehicletracking.auth.presentation.login

data class LoginScreenState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val visiblePassword: Boolean = false
)