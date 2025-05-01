package cz.kudladev.vehicletracking.core.domain

data class ValidationResult(
    val isSuccessful: Boolean,
    val errorMessage: String? = null
)
