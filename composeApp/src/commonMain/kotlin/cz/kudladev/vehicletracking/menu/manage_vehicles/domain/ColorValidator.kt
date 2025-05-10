package cz.kudladev.vehicletracking.menu.manage_vehicles.domain

import cz.kudladev.vehicletracking.core.domain.use_cases.ValidationResult

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