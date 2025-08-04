package cz.kudladev.vehicletracking.feature.history

sealed interface HistoryScreenAction {

    data object Refresh : HistoryScreenAction

}