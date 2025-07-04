package cz.kudladev.vehicletracking.feature.onboarding.login

sealed interface LoginScreenAction {

    data class OnEmailChanged(val email: String) : LoginScreenAction
    data class OnPasswordChanged(val password: String) : LoginScreenAction
    data class OnPasswordVisibleChanged(val visible: Boolean) : LoginScreenAction

    object OnLogin : LoginScreenAction

}