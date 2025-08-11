package cz.kudladev.vehicletracking.feature.search

import cz.kudladev.vehicletracking.model.UiState
import cz.kudladev.vehicletracking.model.Vehicle

data class SearchScreenState(
    val searchQuery: String = "",
    val searchResult: UiState<List<Vehicle>> = UiState.Success(emptyList()),
)