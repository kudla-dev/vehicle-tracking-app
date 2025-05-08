package cz.kudladev.vehicletracking.vehicle_list

sealed interface VehicleListAction {

    data object ToggleView: VehicleListAction

}