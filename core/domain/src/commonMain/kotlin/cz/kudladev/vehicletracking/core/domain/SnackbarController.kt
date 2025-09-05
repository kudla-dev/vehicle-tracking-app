package cz.kudladev.vehicletracking.core.domain

import cz.kudladev.vehicletracking.model.Snackbar
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object SnackbarController {

    private val _events = Channel<Snackbar>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: Snackbar) {
        _events.send(event)
    }


}