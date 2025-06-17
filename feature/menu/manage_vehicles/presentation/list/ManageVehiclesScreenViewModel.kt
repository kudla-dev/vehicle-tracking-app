package cz.kudladev.vehicletracking.menu.manage_vehicles.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import app.cash.paging.cachedIn
import cz.kudladev.vehicletracking.core.domain.vehicles.VehiclePagingSource
import cz.kudladev.vehicletracking.core.domain.vehicles.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class ManageVehiclesScreenViewModel(
    private val vehicleRepository: VehicleRepository,
) : ViewModel() {


    val vehicles = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
        )
    ) {
        VehiclePagingSource(
            vehicleRepository = vehicleRepository,
            brandId = _state.value.selectedBrand?.id,
//            place = _state.value.place
        )
    }.flow.cachedIn(viewModelScope)


    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ManageVehiclesScreenState())
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
            initialValue = ManageVehiclesScreenState()
        )

    fun onAction(action: ManageVehiclesScreenAction) {
        when (action) {
            else -> TODO("Handle actions")
        }
    }

}