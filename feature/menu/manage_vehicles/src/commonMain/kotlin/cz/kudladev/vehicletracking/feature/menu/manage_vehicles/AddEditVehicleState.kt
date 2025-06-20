package cz.kudladev.vehicletracking.feature.menu.manage_vehicles

import cz.kudladev.vehicletracking.model.Brand
import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.Image


data class AddEditVehicleState(
    val images: List<Image> = emptyList(),
    val imagesError: String? = null,
    val fullName: String = "",
    val fullNameError: String? = null,
    val type: String = "",
    val typeError: String? = null,
    val types: List<String> = emptyList(),
    val subType: String = "",
    val subTypeError: String? = null,
    val subTypes: List<String> = emptyList(),
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
    val driverLicenseError: String? = null,
    val selectingDriverLicense: Boolean = false,
    val vehicleURL: String = "",
    val vehicleURLError: ErrorMessage? = null,
    val imageDialog: Boolean = false,
    val resultState: AddEditResultState = AddEditResultState.Empty,
)

sealed class AddEditResultState {
    data object Empty : AddEditResultState()
    data object Loading : AddEditResultState()
    data object Success : AddEditResultState()
    data class Error(val error: ErrorMessage) : AddEditResultState()
}