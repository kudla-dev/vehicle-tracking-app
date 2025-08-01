package cz.kudladev.vehicletracking.feature.menu.protocols

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import cz.kudladev.vehicletracking.core.domain.images.ImageRepository
import cz.kudladev.vehicletracking.core.domain.tracking.TrackingRepository
import cz.kudladev.vehicletracking.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProtocolsViewModel(
    private val trackingRepository: TrackingRepository,
    private val imageRepository: ImageRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val type = savedStateHandle.toRoute<Protocols>().type
    val trackingId = savedStateHandle.toRoute<Protocols>().trackingId
    val trackingState = savedStateHandle.toRoute<Protocols>().trackingState

    val uploadStatus = imageRepository.getUploadStatus()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ProtocolsState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                /** Load initial data here **/
                imageRepository.clearUploadStatus()
                getTracking(trackingId)
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProtocolsState()
        )

    fun onAction(action: ProtocolsAction) {
        when (action) {
            is ProtocolsAction.AddImage -> {
                _state.update { it.copy(
                    images = it.images + (action.page to ImageWithBytes(bytes = action.image))
                ) }
            }
            is ProtocolsAction.NextPage -> {
                _state.update {
                    it.copy(
                        page = action.page.nextPage()
                    )
                }
            }
            is ProtocolsAction.PreviousPage -> {
                _state.update {
                    it.copy(
                        page = action.page.previousPage()
                    )
                }
            }
            is ProtocolsAction.SetAdditionalNotes -> {
                _state.update { it.copy(
                    additionalNotes = action.notes
                ) }
            }
            is ProtocolsAction.SetTachometerReading -> {
                _state.update { it.copy(
                    tachometerReading = action.reading
                ) }
            }
            ProtocolsAction.SubmitTracking -> {
                updateTracking(
                    trackingId = trackingId,
                    trackingState = trackingState,
                    images = _state.value.images,
                    tachometerReading = _state.value.tachometerReading,
                    additionalNotes = _state.value.additionalNotes
                )
            }
            ProtocolsAction.ClearRecentUploads -> {
                imageRepository.clearUploadStatus()
            }
        }
    }

    private fun updateTracking(
        trackingId: String,
        trackingState: TrackingState,
        images: Map<ProtocolsScreenPage, Image>,
        tachometerReading: String,
        additionalNotes: String
    ) = viewModelScope.launch {
        _state.update { it.copy(
            tracking = UiState.Loading
        ) }
        trackingRepository
            .updateTracking(
                trackingId = trackingId,
                state = trackingState,
                message = additionalNotes.ifEmpty { null },
                tachometer = tachometerReading.toIntOrNull()
            )
            .onSuccess { tracking ->
                _state.update { it.copy(
                    tracking = UiState.Success(tracking)
                ) }
                println("Tracking updated successfully: $tracking")
                images.forEach { (page, image) ->
                    imageRepository
                        .uploadImageToTracking(
                            image = image,
                            trackingId = trackingId,
                            state = trackingState
                        )
                }

            }
            .onError { error ->
                _state.update { it.copy(
                    tracking = UiState.Error(error.message)
                ) }
                println("Error updating tracking: ${error.message}")
            }
    }

    private fun getTracking(trackingId: String) = viewModelScope.launch {
        _state.update { it.copy(
            tracking = UiState.Loading
        ) }
        trackingRepository
            .getTracking(trackingId)
            .onSuccess { tracking ->
                _state.update { it.copy(
                    tracking = UiState.Success(tracking)
                ) }
            }
            .onError { error ->
                _state.update { it.copy(
                    tracking = UiState.Error(error.message ?: "Unknown error")
                ) }
            }
    }





}