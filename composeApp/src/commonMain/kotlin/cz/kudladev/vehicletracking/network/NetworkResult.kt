package cz.kudladev.vehicletracking.network

sealed interface Result<out D, out E> {
    data class Success<out D>(val data: D): Result<D, Nothing>
    data class Error(val error: ErrorMessage):
        Result<Nothing, ErrorMessage>
}

inline fun <T, R> Result<T, ErrorMessage>.map(map: (T) -> R): Result<R, ErrorMessage> {
    return when(this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
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
    }
}
inline fun <T> Result<T, ErrorMessage>.onError(action: (ErrorMessage) -> Unit): Result<T, ErrorMessage> {
    return when(this) {
        is Result.Error -> {
            action(error)
            this
        }
        is Result.Success -> this
    }
}

typealias EmptyResult<E> = Result<Unit, E>