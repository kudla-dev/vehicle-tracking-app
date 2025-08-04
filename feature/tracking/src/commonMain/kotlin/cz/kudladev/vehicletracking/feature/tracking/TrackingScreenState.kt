package cz.kudladev.vehicletracking.feature.tracking

import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.Tracking
import cz.kudladev.vehicletracking.model.UiState

data class TrackingScreenState(
    val currentTracking: UiState<Tracking?> = UiState.Loading,
    val confirmTracking: UiState<Boolean> = UiState.Idle
)
