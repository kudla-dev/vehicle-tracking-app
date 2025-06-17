package cz.kudladev.vehicletracking.menu.admin_settings

data class AdminSettingsState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)