package cz.kudladev.vehicletracking.feature.tracking

sealed interface TrackingScreenAction {

    data object Refresh : TrackingScreenAction

}