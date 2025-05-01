package cz.kudladev.vehicletracking.auth.domain.use_cases

import cz.kudladev.vehicletracking.core.domain.ValidationResult

class LastNameValidation {

    fun execute(lastName: String): ValidationResult{
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