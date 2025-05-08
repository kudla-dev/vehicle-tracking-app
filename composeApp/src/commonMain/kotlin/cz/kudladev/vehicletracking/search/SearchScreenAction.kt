package cz.kudladev.vehicletracking.search

sealed interface SearchScreenAction {

    data class SetSearchQuery(val query: String) : SearchScreenAction


}