package cz.kudladev.vehicletracking.model

sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()

    companion object {
        fun <T> idle(): UiState<T> = Idle
        fun <T> loading(): UiState<T> = Loading
        fun <T> success(data: T): UiState<T> = Success(data)
        fun <T> error(message: String): UiState<T> = Error(message)
    }
}