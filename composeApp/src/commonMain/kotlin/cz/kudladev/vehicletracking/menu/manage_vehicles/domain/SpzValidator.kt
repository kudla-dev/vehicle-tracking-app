package cz.kudladev.vehicletracking.menu.manage_vehicles.domain

import cz.kudladev.vehicletracking.core.domain.use_cases.ValidationResult

class SpzValidator {

    fun execute(spz: String): ValidationResult{
        if (spz.isBlank()) {
            return ValidationResult(
                isSuccessful = false,
                errorMessage = "SPZ cannot be empty"
            )
        }
        if (spz.length < 4) {
            return ValidationResult(
                isSuccessful = false,
                errorMessage = "SPZ must be at least 4 characters long"
            )
        }
        if (spz.length > 11) {
            return ValidationResult(
                isSuccessful = false,
                errorMessage = "SPZ must be at most 11 characters long"
            )
        }
        return ValidationResult(
            isSuccessful = true
        )
    }

}