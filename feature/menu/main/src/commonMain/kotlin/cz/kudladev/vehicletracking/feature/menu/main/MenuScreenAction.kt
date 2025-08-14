package cz.kudladev.vehicletracking.feature.menu.main

sealed interface MenuScreenAction {

    data class ChangeProfilePicture(val image: ByteArray) : MenuScreenAction

}