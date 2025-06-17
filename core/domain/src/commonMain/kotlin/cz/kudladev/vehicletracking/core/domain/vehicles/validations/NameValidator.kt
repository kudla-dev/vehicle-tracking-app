package cz.kudladev.vehicletracking.core.domain.vehicles.validations

import cz.kudladev.vehicletracking.model.ValidationResult


class NameValidator {

    fun execute(name: String): ValidationResult {
        return if (name.isBlank()) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Name cannot be empty"
            )
        } else if (name.length < 3) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Name must be at least 3 characters long"
            )
        } else {
            ValidationResult(isSuccessful = true)
        }
    }
}