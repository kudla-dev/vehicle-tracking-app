package cz.kudladev.vehicletracking.feature.menu.tracking_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import cz.kudladev.vehicletracking.core.domain.auth.UserStateHolder
import cz.kudladev.vehicletracking.core.domain.images.ImageRepository
import cz.kudladev.vehicletracking.core.domain.tracking.TrackingRepository
import cz.kudladev.vehicletracking.model.Tracking
import cz.kudladev.vehicletracking.model.TrackingState
import cz.kudladev.vehicletracking.model.UiState
import cz.kudladev.vehicletracking.model.onError
import cz.kudladev.vehicletracking.model.onSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackingDetailViewModel(
    private val trackingRepository: TrackingRepository,
    private val userStateHolder: UserStateHolder,
    private val imageRepository: ImageRepository,
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

    val user = userStateHolder.user

    val activeUploadImageStates = imageRepository
        .getUploadStatus("tracking_image_upload_${trackingId}_${TrackingState.ACTIVE.state}")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    val returnUploadImageStates = imageRepository
        .getUploadStatus("tracking_image_upload_${trackingId}_${TrackingState.RETURNED.state}")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    fun onAction(action: TrackingDetailAction) {
        when (action) {
            is TrackingDetailAction.ApproveTracking -> {
                updateTracking(
                    trackingId = trackingId,
                    state = TrackingState.APPROVED
                )
            }
            is TrackingDetailAction.RejectTracking -> {
                updateTracking(
                    trackingId = trackingId,
                    state = TrackingState.REJECTED
                )
            }
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
                    tracking = UiState.Success(tracking),
                    activeImage = tracking.stateLogs.firstOrNull { trackingLog -> trackingLog.state == TrackingState.ACTIVE }
                        ?.images
                        ?: emptyList(),
                    returnImage = tracking.stateLogs.firstOrNull { trackingLog -> trackingLog.state == TrackingState.RETURNED }
                        ?.images
                        ?: emptyList(),
                ) }
                getUserTrackingHistory(userId = tracking.user.id)
                updateUploadImagesStatus()
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
                    userTrackingHistory = UiState.Success(trackings),
                ) }
            }
            .onError { error ->
                _state.update { it.copy(
                    userTrackingHistory = UiState.Error(error.message ?: "Unknown error")
                ) }
            }
    }

    private fun updateTracking(
        trackingId: String,
        state: TrackingState,
    ) = viewModelScope.launch {
        _state.update { it.copy(
            updatedTracking = UiState.Loading
        ) }
        trackingRepository
            .updateTracking(
                trackingId = trackingId,
                state = state,
            )
            .onSuccess { tracking ->
                _state.update { it.copy(
                    updatedTracking = UiState.Success(tracking)
                ) }
                refresh()
            }
            .onError { error ->
                _state.update { it.copy(
                    updatedTracking = UiState.Error(error.message ?: "Unknown error")
                ) }
            }
    }

    private fun updateUploadImagesStatus() = viewModelScope.launch {
        launch {
            activeUploadImageStates
                .sample(300)
                .collect { activeUploadImageStates ->
                if (_state.value.tracking is UiState.Success<Tracking>){
                    val activeTrackingLog = (_state.value.tracking as UiState.Success<Tracking>)
                        .data
                        .stateLogs
                        .firstOrNull { trackingLog ->
                            trackingLog.state == TrackingState.ACTIVE
                        }
                    if (activeUploadImageStates.isNotEmpty()) {
                        _state.update { it.copy(
                            activeImage = activeUploadImageStates
                        ) }
                    }
                }
            }
        }
        launch {
            returnUploadImageStates
                .sample(300)
                .collect { returnUploadImageStates ->
                if (_state.value.tracking is UiState.Success<Tracking>){
                    val returnTrackingLog = (_state.value.tracking as UiState.Success<Tracking>)
                        .data
                        .stateLogs
                        .firstOrNull { trackingLog ->
                            trackingLog.state == TrackingState.RETURNED
                        }
                    if (returnUploadImageStates.isNotEmpty()) {
                        _state.update { it.copy(
                            returnImage = returnUploadImageStates
                        ) }
                    }
                }
            }
        }
    }
}