package cz.kudladev.vehicletracking.core.domain.models

import cz.kudladev.vehicletracking.core.data.models.vehicle.VehicleFromUrl

data class VehicleScrape(
    val fullName: String,
    val drivingLicenses: List<String>,
    val imageUrls: List<String>,
)

fun VehicleFromUrl.toDomain(): VehicleScrape {
    return VehicleScrape(
        fullName = fullName,
        drivingLicenses = driverLicense,
        imageUrls = imageUrls
    )
}

fun VehicleScrape.toDTO(): VehicleFromUrl {
    return VehicleFromUrl(
        fullName = fullName,
        driverLicense = drivingLicenses,
        imageUrls = imageUrls
    )
}