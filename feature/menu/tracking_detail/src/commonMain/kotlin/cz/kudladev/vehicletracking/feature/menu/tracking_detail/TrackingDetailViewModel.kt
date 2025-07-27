package cz.kudladev.vehicletracking.feature.menu.tracking_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import cz.kudladev.vehicletracking.core.domain.tracking.TrackingRepository
import cz.kudladev.vehicletracking.model.UiState
import cz.kudladev.vehicletracking.model.onError
import cz.kudladev.vehicletracking.model.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackingDetailViewModel(
    private val trackingRepository: TrackingRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val trackingId: String = savedStateHandle.toRoute<TrackingDetail>().trackingId

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(TrackingDetailState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = TrackingDetailState()
        )

    fun onAction(action: TrackingDetailAction) {
        when (action) {
            else -> TODO("Handle actions")
        }
    }

    fun refresh() = viewModelScope.launch {
        getTrackingDetail(trackingId)
    }

    private fun getTrackingDetail(trackingId: String) = viewModelScope.launch {
        _state.update { it.copy(
            tracking = UiState.Loading,
            userTrackingHistory = UiState.Loading
        ) }
        trackingRepository
            .getTracking(trackingId)
            .onSuccess { tracking ->
                _state.update { it.copy(
                    tracking = UiState.Success(tracking)
                ) }
                getUserTrackingHistory(userId = tracking.user.id)
            }
            .onError { error ->
                _state.update { it.copy(
                    tracking = UiState.Error(error.message ?: "Unknown error")
                ) }
            }
    }

    private fun getUserTrackingHistory(
        userId: String,
    ) = viewModelScope.launch {
        trackingRepository
            .getUserTrackingHistory(
                userId = userId,
                includeActive = true,
            )
            .onSuccess { trackings ->
                _state.update { it.copy(
                    userTrackingHistory = UiState.Success(trackings)
                ) }
            }
            .onError { error ->
                _state.update { it.copy(
                    userTrackingHistory = UiState.Error(error.message ?: "Unknown error")
                ) }
            }
    }

}