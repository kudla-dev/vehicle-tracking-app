package cz.kudladev.vehicletracking.feature.tracking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import cz.kudladev.vehicletracking.core.domain.tracking.TrackingRepository
import cz.kudladev.vehicletracking.model.TrackingState
import cz.kudladev.vehicletracking.model.UiState
import cz.kudladev.vehicletracking.model.getNextState
import cz.kudladev.vehicletracking.model.onError
import cz.kudladev.vehicletracking.model.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackingScreenViewModel(
    private val trackingRepository: TrackingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(TrackingScreenState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                getCurrentTracking()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = TrackingScreenState()
        )

    fun onAction(action: TrackingScreenAction) {
        when (action) {
            TrackingScreenAction.Refresh -> {
                getCurrentTracking()
            }
            is TrackingScreenAction.ConfirmReturn -> {
                confirmTracking(
                    trackingId = action.trackingId,
                    trackingState = action.trackingState
                )
            }
        }
    }

    private fun getCurrentTracking() = viewModelScope.launch {
        _state.update { it.copy(currentTracking = UiState.Loading) }
        trackingRepository
            .getCurrentTracking()
            .onSuccess{ tracking ->
                _state.update { it.copy(currentTracking = UiState.Success(tracking)) }
                println("Current tracking fetched successfully: $tracking")
            }
            .onError { errorMessage ->
                _state.update { it.copy(currentTracking = UiState.Error(errorMessage.message)) }
                println("Error fetching current tracking: $errorMessage")
            }
    }

    private fun confirmTracking(
        trackingId: String,
        trackingState: TrackingState
    ) = viewModelScope.launch {
        _state.update { it.copy(
            confirmTracking = UiState.Loading
        ) }
        trackingRepository
            .updateTracking(
                trackingId = trackingId,
                state = TrackingState.nextState(trackingState),
            )
            .onSuccess { result ->
                _state.update { it.copy(
                    confirmTracking = UiState.Success(true),
                ) }
                getCurrentTracking()
            }
            .onError { error ->
                _state.update { it.copy(
                    confirmTracking = UiState.Error(error.message)
                ) }
            }
    }


}