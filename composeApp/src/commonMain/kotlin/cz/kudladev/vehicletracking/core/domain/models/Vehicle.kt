package cz.kudladev.vehicletracking.core.domain.models

import cz.kudladev.vehicletracking.core.data.models.vehicle.VehicleBasic
import cz.kudladev.vehicletracking.core.data.models.vehicle.VehicleFromUrl

data class Vehicle(
    val id: Int? = null,
    val brand: Brand,
    val fullName: String,
    val color: String,
    val year: String,
    val model: String,
    val spz: String,
    val transferableSpz: Boolean,
    val maximumDistance: Int,
    val totalDistance: Int,
    val place: String,
    val driverLicense: String,
    val images: List<String>
)

fun Vehicle.toBasic(): VehicleBasic {
    return VehicleBasic(
        id = id,
        brand = brand.toDTO(),
        fullName = fullName,
        color = color,
        year = year,
        model = model,
        spz = spz,
        transferableSpz = transferableSpz,
        maximumDistance = maximumDistance,
        totalDistance = totalDistance,
        place = place,
        driverLicense = driverLicense,
        images = images
    )
}

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
        driverLicense = driverLicense,
        images = images
    )
}