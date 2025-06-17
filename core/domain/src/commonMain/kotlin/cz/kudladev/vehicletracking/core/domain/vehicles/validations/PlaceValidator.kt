package cz.kudladev.vehicletracking.core.domain.vehicles.validations

import cz.kudladev.vehicletracking.model.ValidationResult


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