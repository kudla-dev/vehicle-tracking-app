package cz.kudladev.vehicletracking.core.domain.auth.validations

import cz.kudladev.vehicletracking.model.ValidationResult

class PhoneNumberValidation {

    companion object {
        private val PHONE_REGEX = "^\\+?[0-9]{10,15}\$".toRegex()
    }

    fun execute(phoneNumber: String): ValidationResult {
        return if (phoneNumber.isBlank()) {
            ValidationResult(
                isSuccessful = false,
                errorMessage = "Phone number cannot be empty"
            )
        } else if (phoneNumber.matches(PHONE_REGEX)) {
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
