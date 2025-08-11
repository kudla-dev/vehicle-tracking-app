package cz.kudladev.vehicletracking.core.data.vehicles.models

import cz.kudladev.vehicletracking.model.DriverLicense
import kotlinx.serialization.Serializable

@Serializable
data class DriverLicenseDTO(
    val type: String,
)

fun DriverLicenseDTO.toDomain(): DriverLicense{
    return DriverLicense(
        type = type,
    )
}