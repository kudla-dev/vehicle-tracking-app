package cz.kudladev.vehicletracking.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import vehicletracking.model.generated.resources.*

@Serializable
data class TrackingLog(
    val trackingId: String? = null,
    val state: TrackingState,
    val message: String? = null,
    val assignedBy: User? = null,
    val assignedAt: LocalDateTime? = null,
    val images: List<Image>? = null,
)


enum class TrackingState(
    val state: String,
    val displayName: StringResource,
    val message: StringResource? = null,
    val activeIcon: ImageVector = Icons.Filled.HourglassBottom,
    val inactiveIcon: ImageVector = Icons.Outlined.HourglassBottom,
) {
        PENDING("pending", Res.string.pendingState, message = Res.string.pendingStateDescription, activeIcon = Icons.Filled.Pending, inactiveIcon = Icons.Outlined.Pending),
        WAITING_FOR_APPROVAL("waiting_for_approval", Res.string.waitingForApprovalState, Res.string.waitingForApprovalStateDescription,),
        APPROVED("approved", Res.string.approvedState, Res.string.approvedStateDescription, activeIcon = Icons.Filled.Check, inactiveIcon = Icons.Outlined.Check),
        WAITING_FOR_START("waiting_for_start", Res.string.waitingForStartState, Res.string.waitingForStartStateDescription),
        ACTIVE("active", Res.string.activeState, activeIcon = Icons.Filled.Route, inactiveIcon = Icons.Outlined.Route),
        WAITING_FOR_RETURN("waiting_for_return", Res.string.waitingForReturnState, Res.string.waitingForReturnStateDescription),
        RETURNED("returned", Res.string.returnedState, activeIcon = Icons.Filled.Garage, inactiveIcon = Icons.Outlined.Garage),
        REJECTED("rejected", Res.string.rejectedState, activeIcon = Icons.Filled.Block, inactiveIcon = Icons.Outlined.Block),
        FAILED("failed", Res.string.failedState, activeIcon = Icons.Filled.Block, inactiveIcon = Icons.Outlined.Block),
        WAITING_FOR_YOUR_CONFIRMATION("waiting_for_your_confirmation", Res.string.waitingForYourConfirmationState, Res.string.waitingForYourConfirmationStateDescription),
        COMPLETED("completed", Res.string.completedState, activeIcon = Icons.Filled.Check, inactiveIcon = Icons.Outlined.Check),
        ERROR("error", Res.string.errorState, message =Res.string.errorStateDescription, activeIcon = Icons.Filled.Error, inactiveIcon = Icons.Outlined.Error);

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