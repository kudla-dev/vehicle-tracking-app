package cz.kudladev.vehicletracking.core.domain.models

import cz.kudladev.vehicletracking.core.data.models.brand.BrandDTO

data class Brand(
    val id: Long,
    val name: String,
    val logoURL: String,
){
    override fun toString(): String {
        return name
    }
}

fun Brand.toDTO(): BrandDTO {
    return BrandDTO(
        id = id,
        name = name,
        imageURL = logoURL
    )
}

fun BrandDTO.toDomain(): Brand {
    return Brand(
        id = id,
        name = name,
        logoURL = imageURL
    )
}