package cz.kudladev.vehicletracking.core.data.brands.models

import kotlinx.serialization.Serializable
import cz.kudladev.vehicletracking.model.Brand

@Serializable
data class BrandDTO(
    val id: Long,
    val name: String,
    val imageURL: String,
)

fun BrandDTO.toDomain(): Brand {
    return Brand(
        id = id,
        name = name,
        logoURL = imageURL,
    )
}