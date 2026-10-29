package cz.kudladev.vehicletracking.network

import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.Result
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.request
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.coroutines.coroutineContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

suspend inline fun <reified T> safeCall(execute: () -> HttpResponse): Result<T, ErrorMessage> = try {
    responseToResult(execute())
} catch (e: CancellationException) {
    throw e
} catch (e: SocketTimeoutException) {
    coroutineContext.ensureActive()
    clientError(408, "The request timed out. Please try again.")
} catch (e: UnresolvedAddressException) {
    coroutineContext.ensureActive()
    clientError(503, "Unable to reach the server. Please check your connection.")
} catch (e: Exception) {
    coroutineContext.ensureActive()
    clientError(503, "The request could not be completed. Please try again.")
}

suspend inline fun <reified T> responseToResult(response: HttpResponse): Result<T, ErrorMessage> {
    if (response.status.value !in 200..299) return parseErrorMessage(response)
    return try {
        if (response.status.value == 204) {
            when {
                null is T -> Result.Success(null as T)
                T::class == Unit::class -> Result.Success(Unit as T)
                else -> clientError(502, "The server returned an unexpected empty response.", response.request.url.encodedPath)
            }
        } else Result.Success(response.body<T>())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        clientError(502, "The server response could not be read.", response.request.url.encodedPath)
    }
}

/** Tolerates older Spring errors, security responses and non-JSON gateways without logging bodies. */
suspend fun parseErrorMessage(response: HttpResponse): Result.Error {
    val fallback = clientError(response.status.value, response.status.description, response.request.url.encodedPath)
    return try {
        // Bound error-body reads, not only the displayed message. A gateway may return a huge page.
        val bytes = ByteArray(16 * 1024)
        val channel = response.bodyAsChannel()
        var size = 0
        while (size < bytes.size) {
            val count = channel.readAvailable(bytes, size, bytes.size - size)
            if (count < 0) break
            size += count
        }
        if (size == bytes.size) {
            channel.cancel()
            return fallback
        }
        val body = Json.parseToJsonElement(bytes.decodeToString(0, size)).jsonObject
        val message = body["message"]?.jsonPrimitive?.contentOrNull?.take(512)?.takeIf { it.isNotBlank() }
        val error = body["error"]?.jsonPrimitive?.contentOrNull?.take(80)?.takeIf { it.isNotBlank() }
        Result.Error(fallback.error.copy(message = message ?: fallback.error.message, error = error ?: fallback.error.error))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        fallback
    }
}

@PublishedApi
@OptIn(ExperimentalTime::class)
internal fun clientError(status: Int, message: String, path: String = "") = Result.Error(
    ErrorMessage("RequestError", message, path, status, Clock.System.now().toString())
)

inline fun <T, R> Result<T, ErrorMessage>.mapSuccess(transform: (T) -> R): Result<R, ErrorMessage> = when (this) {
    is Result.Success -> try {
        Result.Success(transform(data))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        clientError(502, "The server response could not be interpreted.")
    }
    is Result.Error -> this
    Result.Loading -> Result.Loading
}
