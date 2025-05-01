package cz.kudladev.vehicletracking.auth.domain.use_cases

import cz.kudladev.vehicletracking.core.domain.ValidationResult

class PasswordValidation {

    fun execute(password: String): ValidationResult {
        return if (password.length <= 8) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Password must be at least 8 characters long"
            )
        } else if (!password.any { it.isDigit() }) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Password must contain at least one digit"
            )
        } else if (!password.any { it.isUpperCase() }) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Password must contain at least one uppercase letter"
            )
        } else if (!password.any { it.isLetterOrDigit() }) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Password must contain at least one special character"
            )
        } else {
            ValidationResult(
                isSuccessful = true
            )
        }
    }

}