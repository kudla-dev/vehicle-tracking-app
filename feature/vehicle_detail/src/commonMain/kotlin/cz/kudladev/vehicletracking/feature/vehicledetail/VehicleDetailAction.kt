package cz.kudladev.vehicletracking.feature.vehicledetail

import kotlinx.datetime.LocalDateTime

interface VehicleDetailAction {

    data object RefreshVehicle : VehicleDetailAction

    data class OnStartEndDateTimeChange(
        val startLocalDateTime: LocalDateTime?,
        val endLocalDateTime: LocalDateTime?
    ) : VehicleDetailAction

    data object CreateTracking : VehicleDetailAction

}