package cz.kudladev.vehicletracking.auth.domain.use_cases

import cz.kudladev.vehicletracking.core.domain.use_cases.ValidationResult

class FirstNameValidation {

    fun execute(firstName: String): ValidationResult {
        return if (firstName.length > 1 && firstName.length < 20) {
            ValidationResult(
                isSuccessful = true
            )
        } else {
            ValidationResult(
                isSuccessful = false,
                errorMessage = if (firstName.isEmpty()){
                    "First name cannot be empty"
                } else if (firstName.length >= 20) {
                    "First name cannot be longer than 20 characters"
                } else {
                    "First name cannot be shorter than 1 character"
                }
            )
        }
    }

}