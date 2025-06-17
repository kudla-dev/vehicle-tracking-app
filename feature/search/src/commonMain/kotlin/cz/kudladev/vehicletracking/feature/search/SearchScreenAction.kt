package cz.kudladev.vehicletracking.feature.search

sealed interface SearchScreenAction {

    data class SetSearchQuery(val query: String) : SearchScreenAction


}