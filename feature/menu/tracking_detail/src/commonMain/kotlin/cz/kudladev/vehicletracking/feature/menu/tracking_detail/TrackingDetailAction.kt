package cz.kudladev.vehicletracking.feature.menu.tracking_detail

sealed interface TrackingDetailAction {

    data class RejectTracking(
        val trackingId: String,
    ) : TrackingDetailAction

    data class ApproveTracking(
        val trackingId: String,
    ) : TrackingDetailAction

}