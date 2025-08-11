package cz.kudladev.vehicletracking.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Garage
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Garage
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Route
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class TrackingLog(
    val trackingId: String? = null,
    val state: TrackingState,
    val message: String? = null,
    val assignedBy: User? = null,
    val assignedAt: LocalDateTime? = null,
    val images: List<Image>? = null,
)


enum class TrackingState(val state: String, val displayName: String, val message: String? = null, val activeIcon: ImageVector = Icons.Filled.HourglassBottom, val inactiveIcon: ImageVector = Icons.Outlined.HourglassBottom) {
        PENDING("pending", "Pending", activeIcon = Icons.Filled.Pending, inactiveIcon = Icons.Outlined.Pending),
        WAITING_FOR_APPROVAL("waiting_for_approval", "Waiting for Approval", "Please wait, your request is being processed"),
        APPROVED("approved", "Approved", activeIcon = Icons.Filled.Check, inactiveIcon = Icons.Outlined.Check),
        WAITING_FOR_START("waiting_for_start", "Waiting for Start", "You can come for the vehicle at the requested time"),
        ACTIVE("active", "Active", activeIcon = Icons.Filled.Route, inactiveIcon = Icons.Outlined.Route),
        WAITING_FOR_RETURN("waiting_for_return", "Waiting for Return", "Enjoy your trip, please return the vehicle on time"),
        RETURNED("returned", "Returned", activeIcon = Icons.Filled.Garage, inactiveIcon = Icons.Outlined.Garage),
        REJECTED("rejected", "Rejected", activeIcon = Icons.Filled.Block, inactiveIcon = Icons.Outlined.Block),
        FAILED("failed", "Failed", activeIcon = Icons.Filled.Block, inactiveIcon = Icons.Outlined.Block),
        WAITING_FOR_YOUR_CONFIRMATION("waiting_for_your_confirmation", "Waiting for Your Confirmation", "Please confirm the state of the tracking"),
        COMPLETED("completed", "Completed", activeIcon = Icons.Filled.Check, inactiveIcon = Icons.Outlined.Check),
        ERROR("error", "Error", message = "An error occurred while processing the tracking", activeIcon = Icons.Filled.Error, inactiveIcon = Icons.Outlined.Error);

    companion object {
        fun nextState(state: TrackingState): TrackingState {
            return when (state) {
                PENDING -> APPROVED
                APPROVED -> ACTIVE
                ACTIVE -> RETURNED
                RETURNED -> COMPLETED
                else -> state
            }
        }
    }
}

fun String.toTrackingState(): TrackingState {
    return TrackingState.entries.firstOrNull { it.state == this } ?: TrackingState.ERROR
}

fun TrackingState.getNextState(): TrackingState? {
    return when (this) {
        TrackingState.PENDING -> TrackingState.WAITING_FOR_APPROVAL
        TrackingState.APPROVED -> TrackingState.WAITING_FOR_START
        TrackingState.ACTIVE -> TrackingState.WAITING_FOR_RETURN
        TrackingState.RETURNED, TrackingState.REJECTED, TrackingState.FAILED, TrackingState.ERROR -> TrackingState.WAITING_FOR_YOUR_CONFIRMATION
        else -> null
    }
}