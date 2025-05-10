package cz.kudladev.vehicletracking.menu.manage_vehicles.presentation.add_edit

import cz.kudladev.vehicletracking.core.data.models.image.Image
import cz.kudladev.vehicletracking.core.domain.models.Brand
import cz.kudladev.vehicletracking.network.ErrorMessage

data class AddEditVehicleState(
    val images: List<Image> = emptyList(),
    val imagesError: String? = null,
    val fullName: String = "",
    val fullNameError: String? = null,
    val brands: List<Brand> = emptyList(),
    val brand: Brand? = null,
    val brandError: ErrorMessage? = null,
    val selectingBrand: Boolean = false,
    val name: String = "",
    val nameError: String? = null,
    val model: String = "",
    val modelError: String? = null,
    val year: String = "",
    val yearError: String? = null,
    val color: String = "",
    val colorError: String? = null,
    val spz: String = "",
    val spzError: String? = null,
    val transferableSpz: Boolean = false,
    val maximumDistance: String = "",
    val maximumDistanceError: String? = null,
    val totalDistance: String = "",
    val place: String = "Ostrava",
    val selectingPlace: Boolean = false,
    val selectedPlaceError: String? = null,
    val driverLicense: String = "",
    val vehicleURL: String = "",
    val vehicleURLError: ErrorMessage? = null,
    val imageDialog: Boolean = false,
)