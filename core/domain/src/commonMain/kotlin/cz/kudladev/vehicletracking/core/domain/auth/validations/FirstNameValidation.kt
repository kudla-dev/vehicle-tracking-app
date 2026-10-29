package cz.kudladev.vehicletracking.core.domain.auth.validations

import cz.kudladev.vehicletracking.model.ValidationResult

class FirstNameValidation {

    fun execute(firstName: String): ValidationResult {
        return if (firstName.isBlank()) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "First name cannot be empty"
            )
        } else if (firstName.length < 4) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "First name must be at least 4 characters long"
            )
        } else if (firstName.length > 50) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "First name cannot be longer than 50 characters"
            )
        } else {
            ValidationResult(
                isSuccessful = true
            )
        }
    }
}
