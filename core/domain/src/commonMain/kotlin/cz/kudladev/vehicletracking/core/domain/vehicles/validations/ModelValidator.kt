package cz.kudladev.vehicletracking.core.domain.vehicles.validations

import cz.kudladev.vehicletracking.model.ValidationResult


class ModelValidator {

    fun execute(model: String): ValidationResult {
        return if (model.isBlank()) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Model cannot be empty"
            )
        } else if (model.length < 3) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Model must be at least 3 characters long"
            )
        } else {
            ValidationResult(isSuccessful = true)
        }
    }

}