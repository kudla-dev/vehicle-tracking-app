package cz.kudladev.vehicletracking.core.domain.auth.validations

import cz.kudladev.vehicletracking.model.ValidationResult

class LastNameValidation {

    fun execute(lastName: String): ValidationResult {
        return if (lastName.isBlank()) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Last name cannot be empty"
            )
        } else if (lastName.length < 4) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Last name must be at least 4 characters long"
            )
        } else if (lastName.length > 50) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Last name cannot be longer than 50 characters"
            )
        } else {
            ValidationResult(
                isSuccessful = true
            )
        }
    }
}
