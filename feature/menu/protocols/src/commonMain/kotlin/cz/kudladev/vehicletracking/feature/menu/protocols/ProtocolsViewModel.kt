package cz.kudladev.vehicletracking.feature.menu.protocols

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import cz.kudladev.vehicletracking.core.domain.images.ImageRepository
import cz.kudladev.vehicletracking.core.domain.tracking.TrackingRepository
import cz.kudladev.vehicletracking.model.TrackingState
import cz.kudladev.vehicletracking.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProtocolsViewModel(
    private val trackingRepository: TrackingRepository,
    private val imageRepository: ImageRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val type = savedStateHandle.toRoute<Protocols>().type
    val trackingId = savedStateHandle.toRoute<Protocols>().trackingId

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ProtocolsState())
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
            initialValue = ProtocolsState()
        )

    fun onAction(action: ProtocolsAction) {
        when (action) {
            is ProtocolsAction.AddImage -> {
                _state.update { it.copy(
                    images = it.images + (action.page to action.image)
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
        }
    }

    private fun updateTracking(
        trackingState: TrackingState,
        images: Map<ProtocolsScreenPage, ByteArray>,
        tachometerReading: String,
        additionalNotes: String
    ) = viewModelScope.launch {
        _state.update { it.copy(
            tracking = UiState.Loading
        ) }
        trackingRepository

    }





}