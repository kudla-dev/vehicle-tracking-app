package cz.kudladev.vehicletracking.core.domain.auth.validations

import cz.kudladev.vehicletracking.model.ValidationResult

class EmailValidation {

    companion object {
        private val EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$".toRegex()
    }

    fun execute(email: String): ValidationResult {
        return if (email.isBlank()) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Email cannot be empty"
            )
        } else if (email.matches(EMAIL_REGEX)) {
            ValidationResult(
                isSuccessful = true
            )
        } else {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Email is not valid"
            )
        }
    }
}
