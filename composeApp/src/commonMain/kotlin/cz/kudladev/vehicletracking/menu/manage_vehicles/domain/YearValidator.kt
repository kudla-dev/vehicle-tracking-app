package cz.kudladev.vehicletracking.menu.manage_vehicles.domain

import cz.kudladev.vehicletracking.core.domain.use_cases.ValidationResult

class YearValidator {

    fun execute(year: String): ValidationResult {
        return if (year.isBlank()) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Year cannot be empty"
            )
        } else if (year.length < 4) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Year must be at least 4 characters long"
            )
        } else {
            ValidationResult(
                isSuccessful = true
            )
        }
    }

}