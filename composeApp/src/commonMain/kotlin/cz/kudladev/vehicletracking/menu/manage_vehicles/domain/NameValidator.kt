package cz.kudladev.vehicletracking.menu.manage_vehicles.domain

import cz.kudladev.vehicletracking.core.domain.use_cases.ValidationResult

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