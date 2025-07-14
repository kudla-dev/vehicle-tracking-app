package cz.kudladev.vehicletracking.feature.menu.manage_trackings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.cash.paging.*
import cz.kudladev.vehicletracking.core.domain.tracking.TrackingPagingSource
import cz.kudladev.vehicletracking.core.domain.tracking.TrackingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class ManageTrackingsViewModel(
    private val trackingRepository: TrackingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val trackingType = savedStateHandle.toRoute<ManageTrackings>().type

    val trackings = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
        )
    ) {
        TrackingPagingSource(
            trackingRepository = trackingRepository,
            states = trackingType.states
        )
    }.flow.cachedIn(viewModelScope)

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ManageTrackingsState())
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
            initialValue = ManageTrackingsState()
        )

    fun onAction(action: ManageTrackingsAction) {
        when (action) {
            else -> TODO("Handle actions")
        }
    }

}