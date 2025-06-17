package cz.kudladev.vehicletracking.core.domain.vehicles.validations

import cz.kudladev.vehicletracking.model.ValidationResult


class FullNameValidator {

    fun execute(fullName: String): ValidationResult {
        return if (fullName.isBlank()) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Full name cannot be empty"
            )
        } else if (fullName.length < 3) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Full name must be at least 3 characters long"
            )
        } else {
            ValidationResult(isSuccessful = true)
        }
    }

}