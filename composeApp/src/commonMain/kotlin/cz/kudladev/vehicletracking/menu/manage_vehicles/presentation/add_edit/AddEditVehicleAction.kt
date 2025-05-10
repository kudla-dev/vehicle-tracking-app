package cz.kudladev.vehicletracking.menu.manage_vehicles.presentation.add_edit

import cz.kudladev.vehicletracking.core.domain.models.Brand

sealed interface AddEditVehicleAction {

    data object ToggleImageDialog : AddEditVehicleAction
    data class OnNewImageAdd(val byteArray: ByteArray): AddEditVehicleAction
    data class OnImageRemove(val index: Int): AddEditVehicleAction
    data class OnImageClick(val index: Int): AddEditVehicleAction

    data class OnFullNameChange(val fullName: String) : AddEditVehicleAction

    data object ToggleBrandDialog : AddEditVehicleAction
    data class OnBrandChange(val brand: Brand) : AddEditVehicleAction

    data class OnNameChange(val name: String) : AddEditVehicleAction
    data class OnModelChange(val model: String) : AddEditVehicleAction
    data class OnYearChange(val year: String) : AddEditVehicleAction
    data class OnColorChange(val color: String) : AddEditVehicleAction
    data class OnSpzChange(val spz: String) : AddEditVehicleAction
    object OnTransferableSpzChange : AddEditVehicleAction
    data class OnMaximumDistanceChange(val maximumDistance: String) : AddEditVehicleAction
    data class OnTotalDistanceChange(val totalDistance: String) : AddEditVehicleAction
    data class OnPlaceChange(val place: String) : AddEditVehicleAction
    data object TogglePlaceDialog: AddEditVehicleAction
    data class OnDriverLicenseChange(val driverLicense: String) : AddEditVehicleAction
    data class OnVehicleURLChange(val motorcycleURL: String) : AddEditVehicleAction

    data object OnVehicleURLConfirm : AddEditVehicleAction

    object OnSaveClick : AddEditVehicleAction

}