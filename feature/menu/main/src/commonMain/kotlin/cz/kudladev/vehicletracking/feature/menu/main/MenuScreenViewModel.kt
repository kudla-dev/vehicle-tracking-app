package cz.kudladev.vehicletracking.feature.menu.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.kudladev.vehicletracking.core.domain.tracking.TrackingRepository
import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.TrackingState
import cz.kudladev.vehicletracking.model.onError
import cz.kudladev.vehicletracking.model.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MenuScreenViewModel(
    private val trackingRepository: TrackingRepository
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(MenuScreenState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                getCountByState(
                    state = TrackingState.PENDING,
                    onSuccess = { newCount ->
                        _state.update { it.copy(
                            newRequestsCount = newCount
                        ) }
                    },
                    onError = { errorMessage ->
                        _state.update { it.copy(
                            newRequestsCount = null
                        ) }
                    }
                )
                getCountByState(
                    state = TrackingState.APPROVED,
                    onSuccess = { newCount ->
                        _state.update { it.copy(
                            readyToStartCount = newCount
                        ) }
                    },
                    onError = { errorMessage ->
                        _state.update { it.copy(
                            readyToStartCount = null
                        ) }
                    }
                )
                getCountByState(
                    state = TrackingState.ACTIVE,
                    onSuccess = { newCount ->
                        _state.update { it.copy(
                            activeCount = newCount
                        ) }
                    },
                    onError = { errorMessage ->
                        _state.update { it.copy(
                            activeCount = null
                        ) }
                    }
                )

                /** Load initial data here **/
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = MenuScreenState()
        )

    fun onAction(action: MenuScreenAction) {
        when (action) {
            is MenuScreenAction.ChangeProfilePicture -> TODO()
        }
    }

    private fun getCountByState(
        state: TrackingState,
        onSuccess: (newCount: Int) -> Unit,
        onError: (errorMessage: ErrorMessage) -> Unit
    ) = viewModelScope.launch {
        trackingRepository
            .getCountByState(state)
            .onSuccess { newCount ->
                onSuccess(newCount)
            }
            .onError { errorMessage ->
                onError(errorMessage)
            }
    }

    private fun uploadProfilePicture(
        image: ByteArray
    ) = viewModelScope.launch {

    }

}