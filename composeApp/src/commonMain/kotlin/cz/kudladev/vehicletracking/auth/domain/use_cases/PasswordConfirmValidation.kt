package cz.kudladev.vehicletracking.auth.domain.use_cases

import cz.kudladev.vehicletracking.core.domain.use_cases.ValidationResult

class PasswordConfirmValidation {

    fun execute(password: String, confirmPassword: String): ValidationResult {
        return if (password != confirmPassword || password.isBlank() || confirmPassword.isBlank()) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Passwords do not match"
            )
        } else {
            ValidationResult(
                isSuccessful = true
            )
        }
    }

}