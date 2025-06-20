package cz.kudladev.vehicletracking.feature.vehicledetail

import cz.kudladev.vehicletracking.model.Vehicle
import kotlinx.datetime.LocalDateTime


data class VehicleDetailState(
    val vehicle: VehicleLoadingState = VehicleLoadingState.Loading,
    val startLocalDateTime: LocalDateTime? = null,
    val endLocalDateTime: LocalDateTime? = null,
    val trackingCreatingState: TrackingCreatingState = TrackingCreatingState.NotStarted
)


sealed class VehicleLoadingState {
    data object Loading : VehicleLoadingState()
    data class Success(val vehicle: Vehicle) : VehicleLoadingState()
    data class Error(val message: String) : VehicleLoadingState()
    data object Refreshing : VehicleLoadingState()
}


sealed class TrackingCreatingState {
    data object NotStarted : TrackingCreatingState()
    data object Loading : TrackingCreatingState()
    data object Success : TrackingCreatingState()
    data class Error(val message: String) : TrackingCreatingState()
}