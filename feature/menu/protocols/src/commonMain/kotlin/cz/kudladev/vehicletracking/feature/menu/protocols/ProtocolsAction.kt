package cz.kudladev.vehicletracking.feature.menu.protocols

sealed interface ProtocolsAction {

    data class AddImage(
        val page: ProtocolsScreenPage,
        val image: ByteArray
    ) : ProtocolsAction

    data class NextPage(
        val page: ProtocolsScreenPage
    ): ProtocolsAction

    data class PreviousPage(
        val page: ProtocolsScreenPage
    ): ProtocolsAction

    data class SetTachometerReading(
        val reading: String
    ): ProtocolsAction

    data class SetAdditionalNotes(
        val notes: String
    ): ProtocolsAction

    data object SubmitTracking : ProtocolsAction

}