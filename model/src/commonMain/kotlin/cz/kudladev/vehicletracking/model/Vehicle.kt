package cz.kudladev.vehicletracking.model

import kotlinx.serialization.Serializable

@Serializable
data class Vehicle(
    val id: Int? = null,
    val brand: Brand? = null,
    val fullName: String = "",
    val color: String = "",
    val year: String = "",
    val model: String = "",
    val spz: String = "",
    val transferableSpz: Boolean = false,
    val maximumDistance: Int = 0,
    val totalDistance: Int = 0,
    val place: String = "",
    val driverLicense: String = "",
    val images: List<String> = emptyList()
)

//fun Vehicle.toBasic(): VehicleBasic {
//    return VehicleBasic(
//        id = id,
//        brand = brand.toDTO(),
//        fullName = fullName,
//        color = color,
//        year = year,
//        model = model,
//        spz = spz,
//        transferableSpz = transferableSpz,
//        maximumDistance = maximumDistance,
//        totalDistance = totalDistance,
//        place = place,
//        driverLicense = driverLicense,
//        images = images
//    )
//}
//
//fun VehicleBasic.toDomain(): Vehicle {
//    return Vehicle(
//        id = id,
//        brand = brand.toDomain(),
//        fullName = fullName,
//        color = color,
//        year = year,
//        model = model,
//        spz = spz,
//        transferableSpz = transferableSpz,
//        maximumDistance = maximumDistance,
//        totalDistance = totalDistance,
//        place = place,
//        driverLicense = driverLicense,
//        images = images
//    )
//}