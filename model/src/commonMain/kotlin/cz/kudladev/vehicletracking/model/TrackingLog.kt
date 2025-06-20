package cz.kudladev.vehicletracking.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class TrackingLog(
        val trackingId: String? = null,
        val state: TrackingState,
        val message: String? = null,
        val assignedBy: User? = null,
        val assignedAt: LocalDateTime? = null,
        val images: List<String>? = null,
)


enum class TrackingState(val state: String, val displayName: String, val message: String? = null) {
        PENDING("pending", "Pending"),
        WAITING_FOR_APPROVAL("waiting_for_approval", "Waiting for Approval", "Please wait, your request is being processed"),
        APPROVED("approved", "Approved"),
        WAITING_FOR_START("waiting_for_start", "Waiting for Start", "You can come for the vehicle at the requested time"),
        ACTIVE("active", "Active"),
        WAITING_FOR_RETURN("waiting_for_return", "Waiting for Return", "Enjoy your trip, please return the vehicle on time"),
        RETURNED("returned", "Returned"),
        REJECTED("rejected", "Rejected"),
        FAILED("failed", "Failed"),
        WAITING_FOR_YOUR_CONFIRMATION("waiting_for_your_confirmation", "Waiting for Your Confirmation", "Please confirm the state of the tracking"),
        COMPLETED("completed", "Completed"),
        ERROR("error", "Error");
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