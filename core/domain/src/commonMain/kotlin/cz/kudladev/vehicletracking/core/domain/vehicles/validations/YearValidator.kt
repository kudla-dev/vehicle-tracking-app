package cz.kudladev.vehicletracking.core.domain.vehicles.validations

import cz.kudladev.vehicletracking.model.ValidationResult


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