package cz.kudladev.vehicletracking.core.domain.auth.validations

import cz.kudladev.vehicletracking.model.ValidationResult

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