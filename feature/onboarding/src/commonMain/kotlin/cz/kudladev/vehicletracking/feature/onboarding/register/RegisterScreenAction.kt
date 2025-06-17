package cz.kudladev.vehicletracking.feature.onboarding.register

sealed interface RegisterScreenAction {

    data class OnFirstNameChanged(val firstName: String) : RegisterScreenAction
    data class OnLastNameChanged(val lastName: String) : RegisterScreenAction
    data class OnPhoneNumberChanged(val phoneNumber: String) : RegisterScreenAction
    data class OnEmailChanged(val email: String) : RegisterScreenAction
    data class OnPasswordChanged(val password: String) : RegisterScreenAction
    data class OnConfirmPasswordChanged(val confirmPassword: String) : RegisterScreenAction
    data class OnPasswordVisibleChanged(val visible: Boolean) : RegisterScreenAction

    object OnRegister : RegisterScreenAction

}