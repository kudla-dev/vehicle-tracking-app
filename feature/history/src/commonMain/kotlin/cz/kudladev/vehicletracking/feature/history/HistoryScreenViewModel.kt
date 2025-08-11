package cz.kudladev.vehicletracking.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.paging.Pager
import app.cash.paging.cachedIn
import cz.kudladev.vehicletracking.core.domain.auth.UserStateHolder
import cz.kudladev.vehicletracking.core.domain.tracking.TrackingPagingSource
import cz.kudladev.vehicletracking.core.domain.tracking.TrackingRepository
import cz.kudladev.vehicletracking.model.TrackingState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class HistoryScreenViewModel(
    private val trackingRepository: TrackingRepository,
    private val userStateHolder: UserStateHolder
) : ViewModel() {

    private var hasLoadedInitialData = false

    val user = userStateHolder
        .user
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val trackings = user
        .filterNotNull()
        .map { user ->
            Pager(
                config = app.cash.paging.PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false,
                )
            ) {
                TrackingPagingSource(
                    trackingRepository = trackingRepository,
                    states = listOf(TrackingState.COMPLETED, TrackingState.REJECTED, TrackingState.FAILED),
                    userId = user.id,
                )
            }.flow
        }
        .flatMapLatest { it }
        .cachedIn(viewModelScope)

    private val _state = MutableStateFlow(HistoryScreenState())
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
            initialValue = HistoryScreenState()
        )

    fun onAction(action: HistoryScreenAction) {
        when (action) {
            else -> TODO("Handle actions")
        }
    }

}