package cz.kudladev.vehicletracking.feature.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.kudladev.vehicletracking.core.domain.vehicles.VehicleRepository
import cz.kudladev.vehicletracking.model.UiState
import cz.kudladev.vehicletracking.model.Vehicle
import cz.kudladev.vehicletracking.model.onError
import cz.kudladev.vehicletracking.model.onSuccess
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class SearchScreenViewModel(
    private val vehicleRepository: VehicleRepository,
//    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(SearchScreenState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                search()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SearchScreenState()
        )

    fun onAction(action: SearchScreenAction) {
        when (action) {
            is SearchScreenAction.SetSearchQuery -> {
                _state.update {
                    it.copy(
                        searchQuery = action.query
                    )
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun search() {
        _state
            .map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(300L)
            .onEach { query ->
                searchVehicles(query)
            }
            .launchIn(viewModelScope)
    }


    private fun searchVehicles(query: String) = viewModelScope.launch {
        if (query.isBlank()) {
            _state.update { it.copy(
                searchResult = UiState.Success(emptyList())
            ) }
            return@launch
        }
        _state.update { it.copy(
            searchResult = UiState.Loading
        ) }
        vehicleRepository
            .get(
                page = 1,
                size = 10,
                search = query
            )
            .onSuccess { vehicles ->
                _state.update { it.copy(
                    searchResult = UiState.Success(vehicles)
                ) }
            }
            .onError { error ->
                _state.update { it.copy(
                    searchResult = UiState.Error(error.message)
                ) }
            }
    }
}