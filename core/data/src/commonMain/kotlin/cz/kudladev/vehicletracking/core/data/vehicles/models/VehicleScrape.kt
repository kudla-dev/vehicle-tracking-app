package cz.kudladev.vehicletracking.core.data.vehicles.models

import cz.kudladev.vehicletracking.model.DriverLicense
import cz.kudladev.vehicletracking.model.ImageWithUrl
import cz.kudladev.vehicletracking.model.Vehicle
import kotlinx.serialization.Serializable

@Serializable
data class VehicleScrape(
    val fullName: String = "",
    val drivingLicenses: List<String> = emptyList(),
    val imageUrls: List<String> = emptyList(),
)

fun VehicleScrape.toDomain(): Vehicle {
    return Vehicle(
        fullName = fullName,
        driverLicenses = drivingLicenses.map { type ->
            DriverLicense(
                type = type
            )
        }.toMutableSet(),
        images = imageUrls.mapIndexed { index, url ->
            ImageWithUrl(
                url = url,
                position = index
            )
        }
    )
}
