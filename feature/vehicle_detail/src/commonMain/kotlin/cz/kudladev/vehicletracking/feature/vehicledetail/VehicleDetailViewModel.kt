package cz.kudladev.vehicletracking.feature.vehicledetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.kudladev.vehicletracking.core.domain.vehicles.VehicleRepository
import cz.kudladev.vehicletracking.model.onError
import cz.kudladev.vehicletracking.model.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VehicleDetailViewModel(
    private val vehicleRepository: VehicleRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    val vehicleId = 0

    private val _state = MutableStateFlow(VehicleDetailState())
    val state = _state
        .onStart {
            loadVehicle()
        }
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000L),
            initialValue = VehicleDetailState(vehicle = VehicleLoadingState.Loading)
        )

    fun onAction(action: VehicleDetailAction) {
        when (action) {
            is VehicleDetailAction.RefreshVehicle -> {
                TODO()
            }
        }
    }

    private fun loadVehicle() = viewModelScope.launch {
        if (vehicleId == null) {
            _state.value = VehicleDetailState(
                vehicle = VehicleLoadingState.Error("Vehicle is not selected...")
            )
            return@launch
        }
        vehicleRepository
            .getById(vehicleId)
            .onSuccess { vehicle ->
                _state.value = VehicleDetailState(
                    vehicle = VehicleLoadingState.Success(vehicle)
                )
            }
            .onError { error ->
                _state.value = VehicleDetailState(
                    vehicle = VehicleLoadingState.Error(error.message)
                )
            }
    }





}