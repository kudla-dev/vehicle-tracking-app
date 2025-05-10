package cz.kudladev.vehicletracking.menu.manage_vehicles.domain

import cz.kudladev.vehicletracking.core.domain.use_cases.ValidationResult

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