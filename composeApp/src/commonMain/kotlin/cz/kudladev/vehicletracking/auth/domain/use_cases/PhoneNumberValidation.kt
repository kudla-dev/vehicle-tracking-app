package cz.kudladev.vehicletracking.auth.domain.use_cases

import cz.kudladev.vehicletracking.core.domain.ValidationResult

class PhoneNumberValidation {

    fun execute(phoneNumber: String): ValidationResult {
        return if (phoneNumber.matches(Regex("^\\+?[0-9]{10,15}\$"))){
            ValidationResult(
                isSuccessful = true
            )
        } else {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Phone number is not valid"
            )
        }
    }

}