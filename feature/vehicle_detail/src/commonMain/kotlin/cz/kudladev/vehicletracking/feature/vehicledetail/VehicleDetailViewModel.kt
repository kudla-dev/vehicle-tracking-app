package cz.kudladev.vehicletracking.feature.vehicledetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import cz.kudladev.vehicletracking.core.domain.tracking.TrackingRepository
import cz.kudladev.vehicletracking.core.domain.vehicles.VehicleRepository
import cz.kudladev.vehicletracking.model.onError
import cz.kudladev.vehicletracking.model.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VehicleDetailViewModel(
    private val vehicleRepository: VehicleRepository,
    private val trackingRepository: TrackingRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val vehicleId = savedStateHandle.toRoute<VehicleDetails>().vehicleId

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
            is VehicleDetailAction.OnStartEndDateTimeChange -> {
                _state.update { it.copy(
                    startLocalDateTime = action.startLocalDateTime,
                    endLocalDateTime = action.endLocalDateTime
                ) }
            }

            is VehicleDetailAction.CreateTracking -> {
                createTracking()
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

    private fun createTracking() = viewModelScope.launch {
        _state.update { it.copy(trackingCreatingState = TrackingCreatingState.Loading) }
        if (!checkIfTrackingIsValid() && state.value.trackingCreatingState != TrackingCreatingState.Loading) {
            return@launch
        }
        trackingRepository
            .createTracking(
                vehicleId = vehicleId?.toLong() ?: 0L,
                startTime = state.value.startLocalDateTime!!,
                endTime = state.value.endLocalDateTime!!
            )
            .onSuccess {
                _state.update { it.copy(trackingCreatingState = TrackingCreatingState.Success) }
            }
            .onError { error ->
                _state.update { it.copy(trackingCreatingState = TrackingCreatingState.Error(error.message)) }
            }
    }


    private fun checkIfTrackingIsValid(): Boolean {
        val start = state.value.startLocalDateTime
        val end = state.value.endLocalDateTime
        if (start == null && end == null) {
            _state.update { it.copy(
                trackingCreatingState = TrackingCreatingState.Error("Start and end date/time must be selected.")
            ) }
            return false
        } else if (start == null) {
            _state.update { it.copy(
                trackingCreatingState = TrackingCreatingState.Error("Start date/time must be selected.")
            ) }
            return false
        } else if (end == null) {
            _state.update { it.copy(
                trackingCreatingState = TrackingCreatingState.Error("End date/time must be selected.")
            ) }
            return false
        }
        else if (start > end) {
            _state.update {
                it.copy(
                    trackingCreatingState = TrackingCreatingState.Error("Start date/time must be before end date/time.")
                )
            }
            return false
        } else {
            return true
        }
    }



}