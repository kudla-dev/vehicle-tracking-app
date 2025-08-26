package cz.kudladev.vehicletracking.feature.menu.tracking_detail

import cz.kudladev.vehicletracking.model.ImageUploadState
import cz.kudladev.vehicletracking.model.Tracking
import cz.kudladev.vehicletracking.model.UiState

data class TrackingDetailState(
    val tracking: UiState<Tracking> = UiState.Loading,
    val activeImage: List<ImageUploadState> = emptyList(),
    val returnImage: List<ImageUploadState> = emptyList(),
    val userTrackingHistory: UiState<List<Tracking>> = UiState.Loading,
    val updatedTracking: UiState<Tracking> = UiState.Idle,
)

