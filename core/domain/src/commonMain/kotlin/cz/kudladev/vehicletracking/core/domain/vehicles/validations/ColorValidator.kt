package cz.kudladev.vehicletracking.core.domain.vehicles.validations

import cz.kudladev.vehicletracking.model.ValidationResult

class ColorValidator {

    fun execute(color: String): ValidationResult {
        if (color.isBlank()) {
            return ValidationResult(
                isSuccessful = false,
                errorMessage = "Color cannot be empty"
            )
        }
        return ValidationResult(
            isSuccessful = true
        )
    }

}