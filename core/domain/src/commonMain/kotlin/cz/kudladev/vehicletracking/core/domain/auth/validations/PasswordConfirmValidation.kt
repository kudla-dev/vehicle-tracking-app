package cz.kudladev.vehicletracking.core.domain.auth.validations

import cz.kudladev.vehicletracking.model.ValidationResult


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