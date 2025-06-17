package cz.kudladev.vehicletracking.core.domain.vehicles.validations

import cz.kudladev.vehicletracking.model.ValidationResult


class BrandValidator {

    fun execute(brand: String): ValidationResult {
        if (brand.isBlank()) {
            return ValidationResult(
                isSuccessful = false,
                errorMessage = "Brand cannot be empty"
            )
        }
        return ValidationResult(
            isSuccessful = true
        )
    }
}