package cz.kudladev.vehicletracking.model

enum class SnackbarStyle {
    INFO,
    WARNING,
    SUCCESS,
    ERROR,
}

enum class SnackbarLength {
    SHORT,
    LONG,
    INDEFINITE,
}

data class Snackbar(
    val title: String,
    val description: String? = null,
    val actionTitle: String? = null,
    val action: (() -> Unit)? = null,
    val snackbarStyle: SnackbarStyle = SnackbarStyle.INFO,
    val snackbarLength: SnackbarLength = SnackbarLength.SHORT,
)