package cz.kudladev.vehicletracking.feature.vehicledetail

import cz.kudladev.vehicletracking.model.Vehicle


data class VehicleDetailState(
    val vehicle: VehicleLoadingState = VehicleLoadingState.Loading,
)


sealed class VehicleLoadingState {
    data object Loading : VehicleLoadingState()
    data class Success(val vehicle: Vehicle) : VehicleLoadingState()
    data class Error(val message: String) : VehicleLoadingState()
    data object Refreshing : VehicleLoadingState()
}
