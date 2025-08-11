package cz.kudladev.vehicletracking.core.data.vehicles.models

import cz.kudladev.vehicletracking.core.data.brands.models.BrandDTO
import cz.kudladev.vehicletracking.core.data.brands.models.toDomain
import cz.kudladev.vehicletracking.model.ImageWithUrl
import cz.kudladev.vehicletracking.model.Vehicle
import kotlinx.serialization.Serializable

@Serializable
data class VehicleBasic(
    val id: Int?,
    val brand: BrandDTO,
    val fullName: String,
    val color: String,
    val year: String,
    val model: String,
    val spz: String,
    val transferableSpz: Boolean,
    val maximumDistance: Int,
    val totalDistance: Int,
    val place: String,
    val driverLicenses: List<DriverLicenseDTO>,
    val images: List<ImageWithUrl>
)

fun VehicleBasic.toDomain(): Vehicle {
    return Vehicle(
        id = id,
        brand = brand.toDomain(),
        fullName = fullName,
        color = color,
        year = year,
        model = model,
        spz = spz,
        transferableSpz = transferableSpz,
        maximumDistance = maximumDistance,
        totalDistance = totalDistance,
        place = place,
        driverLicenses = driverLicenses.map {
            it.toDomain()
        }.toMutableSet(),
        images = images
    )
}