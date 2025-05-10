package cz.kudladev.vehicletracking.menu.manage_vehicles.domain

import cz.kudladev.vehicletracking.core.domain.use_cases.ValidationResult

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