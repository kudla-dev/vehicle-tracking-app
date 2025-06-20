package cz.kudladev.vehicletracking.feature.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.kudladev.vehicletracking.core.domain.tracking.TrackingRepository
import cz.kudladev.vehicletracking.model.onError
import cz.kudladev.vehicletracking.model.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackingScreenViewModel(
    private val trackingRepository: TrackingRepository
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
        }
    }

    private fun getCurrentTracking() = viewModelScope.launch {
        _state.update { it.copy(currentTracking = CurrentTrackingState.Loading) }
        trackingRepository
            .getCurrentTracking()
            .onSuccess{ tracking ->
                _state.update { it.copy(currentTracking = CurrentTrackingState.Success(tracking)) }
                println("Current tracking fetched successfully: $tracking")
            }
            .onError { errorMessage ->
                _state.update { it.copy(currentTracking = CurrentTrackingState.Error(errorMessage)) }
                println("Error fetching current tracking: $errorMessage")
            }
    }


}