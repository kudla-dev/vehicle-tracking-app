package cz.kudladev.vehicletracking.model

data class ValidationResult(
    val isSuccessful: Boolean,
    val errorMessage: String? = null
)
