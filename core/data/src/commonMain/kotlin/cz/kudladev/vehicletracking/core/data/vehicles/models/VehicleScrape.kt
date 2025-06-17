package cz.kudladev.vehicletracking.core.data.vehicles.models

import cz.kudladev.vehicletracking.model.Vehicle

data class VehicleScrape(
    val fullName: String,
    val drivingLicenses: List<String>,
    val imageUrls: List<String>,
)

fun VehicleScrape.toDomain(): Vehicle {
    return Vehicle(
        fullName = fullName,
        driverLicense = drivingLicenses.joinToString(", "),
        images = imageUrls
    )
}
