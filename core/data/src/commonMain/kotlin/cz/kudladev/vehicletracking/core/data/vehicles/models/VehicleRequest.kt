package cz.kudladev.vehicletracking.menu.manage_vehicles.data

import kotlinx.serialization.Serializable
import cz.kudladev.vehicletracking.core.data.vehicles.models.DriverLicenseDTO

@Serializable
data class VehicleRequest(
    val id: Long? = null,
    val fullName: String,
    val brandId: Long,
    val model: String,
    val year: String,
    val color: String,
    val spz: String,
    val transferableSpz: Boolean,
    val totalDistance: Int,
    val maximumDistance: Int,
    val driverLicense: List<DriverLicenseDTO>,
    val place: String,
)
