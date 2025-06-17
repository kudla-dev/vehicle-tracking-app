package cz.kudladev.vehicletracking.network

import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.Result
import io.ktor.client.call.*
import io.ktor.client.network.sockets.*
import io.ktor.client.statement.*
import io.ktor.util.network.*
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, ErrorMessage> {
    val response = try {
        execute()
    } catch(e: SocketTimeoutException) {
        e.printStackTrace()
        return Result.Error(
            ErrorMessage(
                error = "SocketTimeoutException",
                message = "Your request timed out. Please try again.",
                path = "",
                status = 500,
                timestamp = Clock.System.now().toString()
            )
        )
    } catch(e: UnresolvedAddressException) {
        e.printStackTrace()
        return Result.Error(
            ErrorMessage(
                error = "UnresolvedAddressException",
                message = "Unable to resolve address. Please check your internet connection.",
                path = "",
                status = 500,
                timestamp = Clock.System.now().toString()
            )
        )
    } catch (e: Exception) {
        e.printStackTrace()
        coroutineContext.ensureActive()
        return Result.Error(
            ErrorMessage(
                error = "Exception",
                message = "An unknown error occurred. Please try again.",
                path = "",
                status = 500,
                timestamp = Clock.System.now().toString()
            )
        )
    }

    return responseToResult(response)
}

@OptIn(ExperimentalTime::class)
suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Result<T, ErrorMessage> {
    return when(response.status.value) {
        in 200..299 -> {
            try {
                val message = response.body<T>()
                Result.Success(message)
            } catch (e: Exception) {
                val error = try {
                    response.body<ErrorMessage>()
                } catch (e: NoTransformationFoundException) {
                    ErrorMessage(
                        error = "NoTransformationFoundException",
                        message = "An unknown error occurred. Please try again.",
                        path = "",
                        status = 500,
                        timestamp = Clock.System.now().toString()
                    )
                }
                Result.Error(error)
            }
        }
        else -> {
            val error = try {
                response.body<ErrorMessage>()
            } catch (e: NoTransformationFoundException) {
                ErrorMessage(
                    error = "NoTransformationFoundException",
                    message = "An unknown error occurred. Please try again.",
                    path = "",
                    status = 500,
                    timestamp = Clock.System.now().toString()
                )
            }
            Result.Error(error)
        }
    }
}

inline fun <T, R> Result<T, ErrorMessage>.mapSuccess(transform: (T) -> R): Result<R, ErrorMessage> {
    return when (this) {
        is Result.Success -> Result.Success<R>(transform(data))
        is Result.Error -> Result.Error(error)
        is Result.Loading -> Result.Loading
    }
}
