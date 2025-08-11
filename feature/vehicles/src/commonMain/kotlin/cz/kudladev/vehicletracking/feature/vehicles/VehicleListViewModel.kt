package cz.kudladev.vehicletracking.feature.vehicles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.cachedIn
import cz.kudladev.vehicletracking.core.domain.vehicles.VehiclePagingSource
import cz.kudladev.vehicletracking.core.domain.vehicles.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class VehicleListViewModel(
    savedStateHandle: SavedStateHandle,
    vehicleRepository: VehicleRepository
) : ViewModel() {

    val searchQuery: String? = savedStateHandle.toRoute<VehicleList>().searchQuery

    val vehicles = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
        )
    ) {
        VehiclePagingSource(
            search = searchQuery,
            vehicleRepository = vehicleRepository,
            brandId = _state.value.selectedBrand?.id,
            place = _state.value.place
        )
    }.flow.cachedIn(viewModelScope)

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(VehicleListState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                /** Load initial data here **/
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = VehicleListState()
        )

    fun onAction(action: VehicleListAction) {
        when (action) {
            VehicleListAction.ToggleView -> {
                _state.value = _state.value.copy(
                    selectedView = if (_state.value.selectedView == VehicleListView.Grid) {
                        VehicleListView.List
                    } else {
                        VehicleListView.Grid
                    }
                )
            }
        }
    }




}