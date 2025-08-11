package cz.kudladev.vehicletracking.feature.vehicledetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import cz.kudladev.vehicletracking.core.domain.tracking.TrackingRepository
import cz.kudladev.vehicletracking.core.domain.vehicles.VehicleRepository
import cz.kudladev.vehicletracking.model.UiState
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
            loadVehicle(vehicleId)
        }
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000L),
            initialValue = VehicleDetailState(vehicle = UiState.Loading)
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

    private fun loadVehicle(
        vehicleId: Int?,
    ) = viewModelScope.launch {
        if (vehicleId == null) {
            _state.value = VehicleDetailState(
                vehicle = UiState.Error("Vehicle is not selected...")
            )
            return@launch
        }
        vehicleRepository
            .getById(vehicleId)
            .onSuccess { vehicle ->
                _state.value = VehicleDetailState(
                    vehicle = UiState.Success(vehicle)
                )
                getCalendar(vehicleId = vehicleId)
            }
            .onError { error ->
                _state.value = VehicleDetailState(
                    vehicle = UiState.Error(error.message)
                )
            }
    }

    private fun createTracking() = viewModelScope.launch {
        _state.update { it.copy(trackingCreatingState = UiState.Loading) }
        if (!checkIfTrackingIsValid() && state.value.trackingCreatingState != UiState.Loading) {
            return@launch
        }
        trackingRepository
            .createTracking(
                vehicleId = vehicleId?.toLong() ?: 0L,
                startTime = state.value.startLocalDateTime!!,
                endTime = state.value.endLocalDateTime!!
            )
            .onSuccess {
                _state.update { it.copy(trackingCreatingState = UiState.Success(true)) }
            }
            .onError { error ->
                _state.update { it.copy(trackingCreatingState = UiState.Error(error.message)) }
            }
    }


    private fun checkIfTrackingIsValid(): Boolean {
        val start = state.value.startLocalDateTime
        val end = state.value.endLocalDateTime
        if (start == null && end == null) {
            _state.update { it.copy(
                trackingCreatingState = UiState.Error("Start and end date/time must be selected.")
            ) }
            return false
        } else if (start == null) {
            _state.update { it.copy(
                trackingCreatingState = UiState.Error("Start date/time must be selected.")
            ) }
            return false
        } else if (end == null) {
            _state.update { it.copy(
                trackingCreatingState = UiState.Error("End date/time must be selected.")
            ) }
            return false
        }
        else if (start > end) {
            _state.update {
                it.copy(
                    trackingCreatingState = UiState.Error("Start date/time must be before end date/time.")
                )
            }
            return false
        } else {
            return true
        }
    }

    private fun getCalendar(
        vehicleId: Int
    ) = viewModelScope.launch {
        _state.update { it.copy(
            calendar = UiState.Loading
        ) }
        vehicleRepository
            .getCalendar(
                vehicleId = vehicleId
            )
            .onSuccess { calendar ->
                _state.update { it.copy(
                    calendar = UiState.Success(calendar)
                ) }
            }
            .onError { error ->
                _state.update { it.copy(
                    calendar = UiState.Error(error.message)
                ) }
            }
    }



}