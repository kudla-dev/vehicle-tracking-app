package cz.kudladev.vehicletracking.network

sealed interface Result<out D, out E> {
    data class Success<out D>(val data: D): Result<D, Nothing>
    data class Error(val error: ErrorMessage):
        Result<Nothing, ErrorMessage>
    object Loading: Result<Nothing, Nothing>
}

inline fun <T, R> Result<T, ErrorMessage>.map(map: (T) -> R): Result<R, ErrorMessage> {
    return when(this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
        is Result.Loading -> Result.Loading
    }
}

fun <T> Result<T, ErrorMessage>.asEmptyDataResult(): EmptyResult<ErrorMessage> {
    return map {  }
}

inline fun <T> Result<T, ErrorMessage>.onSuccess(action: (T) -> Unit): Result<T, ErrorMessage> {
    return when(this) {
        is Result.Error -> this
        is Result.Success -> {
            action(data)
            this
        }
        is Result.Loading -> this
    }
}
inline fun <T> Result<T, ErrorMessage>.onError(action: (ErrorMessage) -> Unit): Result<T, ErrorMessage> {
    return when(this) {
        is Result.Error -> {
            action(error)
            this
        }
        is Result.Success -> this
        is Result.Loading -> this
    }
}

typealias EmptyResult<E> = Result<Unit, E>