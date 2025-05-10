package cz.kudladev.vehicletracking.menu.manage_vehicles.domain

import cz.kudladev.vehicletracking.core.domain.use_cases.ValidationResult

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