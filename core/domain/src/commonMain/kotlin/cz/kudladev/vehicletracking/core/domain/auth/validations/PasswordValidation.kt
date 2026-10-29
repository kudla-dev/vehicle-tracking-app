package cz.kudladev.vehicletracking.core.domain.auth.validations

import cz.kudladev.vehicletracking.model.ValidationResult

class PasswordValidation {

    companion object {
        const val SPECIAL_CHARACTERS = "@#$%^&+=!"
    }

    fun execute(password: String): ValidationResult {
        return if (password.length < 8) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Password must be at least 8 characters long"
            )
        } else if (password.any { it.isWhitespace() }) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Password must not contain whitespace"
            )
        } else if (!password.any { it.isDigit() }) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Password must contain at least one digit"
            )
        } else if (!password.any { it.isLowerCase() }) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Password must contain at least one lowercase letter"
            )
        } else if (!password.any { it.isUpperCase() }) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Password must contain at least one uppercase letter"
            )
        } else if (!password.any { it in SPECIAL_CHARACTERS }) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Password must contain at least one special character (@#$%^&+=!)"
            )
        } else {
            ValidationResult(
                isSuccessful = true
            )
        }
    }
}
