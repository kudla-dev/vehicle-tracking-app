package cz.kudladev.vehicletracking.core.domain.use_cases

data class ValidationResult(
    val isSuccessful: Boolean,
    val errorMessage: String? = null
)