package cz.kudladev.vehicletracking.core.domain.vehicles.validations

import cz.kudladev.vehicletracking.model.ValidationResult

class MaximumDistanceValidator {

    fun execute(
        maximumDistance: String,
        totalDistance: String,
    ): ValidationResult {
        val maximumDistanceValue = maximumDistance.toDoubleOrNull()
        val totalDistanceValue = totalDistance.toDoubleOrNull()

        if (maximumDistanceValue == null) {
            return ValidationResult(
                isSuccessful = false,
                errorMessage = "Maximum distance must be a number",
            )
        }

        if (totalDistanceValue == null) {
            return ValidationResult(
                isSuccessful = false,
                errorMessage = "Total distance must be a number",
            )
        }

        if (maximumDistanceValue < totalDistanceValue) {
            return ValidationResult(
                isSuccessful = false,
                errorMessage = "Maximum distance must be greater than total distance",
            )
        }

        return ValidationResult(isSuccessful = true)
    }

}