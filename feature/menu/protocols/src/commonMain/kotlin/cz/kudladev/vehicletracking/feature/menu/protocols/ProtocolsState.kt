package cz.kudladev.vehicletracking.feature.menu.protocols

import cz.kudladev.vehicletracking.model.Image
import cz.kudladev.vehicletracking.model.Tracking
import cz.kudladev.vehicletracking.model.UiState

data class ProtocolsState(
    val page: ProtocolsScreenPage = ProtocolsScreenPage.FRONT,
    val images: Map<ProtocolsScreenPage, Image> = emptyMap(),
    val tachometerReading: String = "",
    val additionalNotes: String = "",
    val tracking: UiState<Tracking> = UiState.Idle,
)

enum class ProtocolsScreenPage(val instruction: String,val title: String) {
    FRONT(
        instruction = "Capture the front view of the vehicle",
        title = "Front View"
    ),
    BACK(
        instruction = "Capture the back view of the vehicle",
        title = "Back View"
    ),
    LEFT(
        instruction = "Capture the left side view of the vehicle",
        title = "Left Side View"
    ),
    RIGHT(
        instruction = "Capture the right side view of the vehicle",
        title = "Right Side View"
    ),
    TACHOMETER(
        instruction = "Capture the tachometer reading",
        title = "Tachometer Reading"
    ),
    SUMMARY(
        instruction = "Review and confirm the captured images and details",
        title = "Summary"
    );

    fun nextPage(): ProtocolsScreenPage {
        return when (this) {
            FRONT -> BACK
            BACK -> LEFT
            LEFT -> RIGHT
            RIGHT -> TACHOMETER
            TACHOMETER -> SUMMARY
            SUMMARY -> FRONT
        }
    }

    fun previousPage(): ProtocolsScreenPage {
        return when (this) {
            FRONT -> SUMMARY
            BACK -> FRONT
            LEFT -> BACK
            RIGHT -> LEFT
            TACHOMETER -> RIGHT
            SUMMARY -> TACHOMETER
        }
    }

    companion object{
        fun viewPages(): List<ProtocolsScreenPage> {
            return listOf(FRONT, BACK, LEFT, RIGHT, TACHOMETER)
        }
    }
}