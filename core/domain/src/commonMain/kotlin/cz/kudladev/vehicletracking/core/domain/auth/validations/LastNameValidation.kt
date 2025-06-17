package cz.kudladev.vehicletracking.core.domain.auth.validations

import cz.kudladev.vehicletracking.model.ValidationResult


class LastNameValidation {

    fun execute(lastName: String): ValidationResult {
        if (lastName.length > 1 && lastName.length < 20) {
            return ValidationResult(
                isSuccessful = true
            )
        } else {
            return ValidationResult(
                isSuccessful = false,
                errorMessage = if (lastName.isEmpty()){
                    "Last name cannot be empty"
                } else if (lastName.length >= 20) {
                    "Last name cannot be longer than 20 characters"
                } else {
                    "Last name cannot be shorter than 1 character"
                }
            )
        }
    }
}