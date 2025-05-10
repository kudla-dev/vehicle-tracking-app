package cz.kudladev.vehicletracking.auth.domain.use_cases

import cz.kudladev.vehicletracking.core.domain.use_cases.ValidationResult

class EmailValidation {

    fun execute(email: String): ValidationResult {
        return if (email.isEmpty()) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Email cannot be empty"
            )
        } else if (email.matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$"))){
            ValidationResult(
                isSuccessful = true
            )
        } else {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Email is not valid"
            )
        }
    }

}