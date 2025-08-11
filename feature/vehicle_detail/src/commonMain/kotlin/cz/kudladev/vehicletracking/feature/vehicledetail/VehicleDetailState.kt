package cz.kudladev.vehicletracking.feature.vehicledetail

import cz.kudladev.vehicletracking.model.UiState
import cz.kudladev.vehicletracking.model.Vehicle
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime


data class VehicleDetailState(
    val vehicle: UiState<Vehicle> = UiState.Loading,
    val calendar: UiState<Map<LocalDate, List<LocalTime>>> = UiState.Loading,
    val startLocalDateTime: LocalDateTime? = null,
    val endLocalDateTime: LocalDateTime? = null,
    val trackingCreatingState: UiState<Boolean> = UiState.Idle
)


