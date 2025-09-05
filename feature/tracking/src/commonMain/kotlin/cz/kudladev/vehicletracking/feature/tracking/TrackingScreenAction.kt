package cz.kudladev.vehicletracking.feature.tracking

import cz.kudladev.vehicletracking.model.TrackingState

sealed interface TrackingScreenAction {

    data object Refresh : TrackingScreenAction

    data class ConfirmReturn(
        val trackingId: String,
        val trackingState: TrackingState
    ) : TrackingScreenAction

    data object ReturnAcknowledge : TrackingScreenAction

}