package cz.kudladev.vehicletracking.core.data.models.brand

import kotlinx.serialization.Serializable

@Serializable
data class BrandDTO(
    val id: Long,
    val name: String,
    val imageURL: String,
)
