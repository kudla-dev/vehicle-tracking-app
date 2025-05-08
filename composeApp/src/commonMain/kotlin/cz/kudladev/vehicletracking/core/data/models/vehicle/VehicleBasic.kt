package cz.kudladev.vehicletracking.core.data.models.vehicle

import cz.kudladev.vehicletracking.core.data.models.brand.BrandDTO
import kotlinx.serialization.Serializable

@Serializable
data class VehicleBasic(
    val id: Int,
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
    val driverLicense: String,
    val images: List<String>
)