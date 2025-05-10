package cz.kudladev.vehicletracking.menu.manage_vehicles.domain

import cz.kudladev.vehicletracking.core.domain.use_cases.ValidationResult

class PlaceValidator {

    fun execute(place: String): ValidationResult {
        if (place.isBlank()) {
            return ValidationResult(
                isSuccessful = false,
                errorMessage = "Place cannot be empty"
            )
        }
        return ValidationResult(
            isSuccessful = true
        )
    }

}