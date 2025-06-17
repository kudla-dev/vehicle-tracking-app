package cz.kudladev.vehicletracking.feature.vehicles

sealed interface VehicleListAction {

    data object ToggleView: VehicleListAction

}