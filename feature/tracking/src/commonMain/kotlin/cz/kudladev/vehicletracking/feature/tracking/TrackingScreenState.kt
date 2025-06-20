package cz.kudladev.vehicletracking.feature.tracking

import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.Tracking

data class TrackingScreenState(
    val currentTracking: CurrentTrackingState = CurrentTrackingState.Loading,
)


sealed class CurrentTrackingState {
    data object Loading : CurrentTrackingState()
    data class Success(val data: Tracking?) : CurrentTrackingState()
    data class Error(val message: ErrorMessage) : CurrentTrackingState()
}