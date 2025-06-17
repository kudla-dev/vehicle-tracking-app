package cz.kudladev.vehicletracking.model

import kotlinx.serialization.Serializable

@Serializable
data class Brand(
    val id: Long,
    val name: String,
    val logoURL: String,
){
    override fun toString(): String {
        return name
    }
}