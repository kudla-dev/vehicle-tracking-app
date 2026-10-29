package cz.kudladev.vehicletracking.core.data.images.upload

import cz.kudladev.vehicletracking.core.domain.vehicles.ProgressUpdate
import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.ImageWithUrl
import cz.kudladev.vehicletracking.model.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.serialization.Serializable

/** Reads only the upload acknowledgement fields; the server returns the parent entity. */
internal class ImageUploadClient(private val client: HttpClient) {
    fun vehicle(bytes: ByteArray, id: Long, position: Int) = vehicleUpload(bytes, id, position, null)
    fun vehicleUrl(url: String, id: Long, position: Int) = vehicleUpload(byteArrayOf(), id, position, url)

    private fun vehicleUpload(bytes: ByteArray, id: Long, position: Int, sourceUrl: String?) = upload(
        path = "/vehicles/$id/images", bytes = bytes, position = position, sourceUrl = sourceUrl,
        existing = {
            val response = client.get("/vehicles/$id")
            if (response.status.value !in 200..299) throw UploadRejected(response.status.value)
            val parent = response.body<VehicleImages>()
            check(parent.id == id) { "Unexpected vehicle" }
            parent.images
        },
        acknowledged = { response ->
            val parent = response.body<VehicleImages>()
            check(parent.id == id) { "Unexpected vehicle" }
            parent.images
        },
    )

    fun tracking(bytes: ByteArray, id: String, position: Int, state: String) = upload(
        path = "/trackings/$id/logs/$state/images", bytes = bytes, position = position,
        existing = {
            val response = client.get("/trackings/$id")
            if (response.status.value !in 200..299) throw UploadRejected(response.status.value)
            val parent = response.body<TrackingImages>()
            check(parent.id == id) { "Unexpected reservation" }
            parent.stateLogs.single { it.state == state }.images.orEmpty()
        },
        acknowledged = { response ->
            val parent = response.body<TrackingImages>()
            check(parent.id == id) { "Unexpected reservation" }
            parent.stateLogs.single { it.state == state }.images.orEmpty()
        },
    )

    private fun upload(
        path: String,
        bytes: ByteArray,
        position: Int,
        sourceUrl: String? = null,
        existing: suspend () -> List<ImageWithUrl>,
        acknowledged: suspend (io.ktor.client.statement.HttpResponse) -> List<ImageWithUrl>,
    ): Flow<Result<ProgressUpdate, ErrorMessage>> = channelFlow {
        try {
            require(bytes.isNotEmpty() || !sourceUrl.isNullOrBlank()) { "Empty image" }
            // Snapshot IDs so an old photo or a parent UUID cannot masquerade as this upload.
            val previousIds = existing().mapNotNull { it.id }.toSet()
            val response = if (sourceUrl != null) client.post("$path/fetch") {
                parameter("url", sourceUrl)
                contentType(ContentType.Application.Json)
                setBody(ImagePosition(position))
            } else client.submitFormWithBinaryData(path, formData {
                append("file", bytes, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"image.jpg\"")
                })
                append("position", position.toString())
            }) {
                parameter("position", position)
                onUpload { sent, total ->
                    if (total != null && total > 0) send(Result.Success(ProgressUpdate(sent, total)))
                }
            }
            if (response.status.value !in 200..299) {
                send(Result.Error(uploadError(response.status.value, "Server rejected image upload", path)))
                return@channelFlow
            }
            // Older servers can repeat the same association after flushing their JPA collection.
            val image = acknowledged(response).distinct().filter {
                !it.id.isNullOrBlank() && !it.url.isNullOrBlank() && it.id !in previousIds
            }.singleOrNull() ?: error("Upload response does not identify exactly one new image")
            val size = bytes.size.toLong().coerceAtLeast(1)
            send(Result.Success(ProgressUpdate(size, size, image)))
        } catch (e: CancellationException) {
            throw e
        } catch (e: UploadRejected) {
            send(Result.Error(uploadError(e.status, "Cannot verify existing photos", path)))
        } catch (e: Exception) {
            // Keep response bodies, credentials and server details out of logs/UI errors.
            send(Result.Error(uploadError(502, "Image upload could not be confirmed", path)))
        }
    }
}

@Serializable
private data class VehicleImages(val id: Long, val images: List<ImageWithUrl>)

@Serializable
private data class TrackingImages(val id: String, val stateLogs: List<LogImages>)

@Serializable
private data class LogImages(val state: String, val images: List<ImageWithUrl>? = null)

internal fun uploadError(status: Int, message: String, path: String = "") =
    ErrorMessage("ImageUpload", message, path, status, "")

/** Sending 100% of bytes is not server acknowledgement. */
internal suspend fun Flow<Result<ProgressUpdate, ErrorMessage>>.awaitUploadedImage(
    onProgress: suspend (ProgressUpdate) -> Unit = {},
): Result<ImageWithUrl, ErrorMessage> {
    var terminal: Result<ImageWithUrl, ErrorMessage>? = null
    collect { result ->
        when (result) {
            is Result.Success -> {
                onProgress(result.data)
                val image = result.data.imageURL
                if (terminal !is Result.Error && !image?.id.isNullOrBlank() && !image?.url.isNullOrBlank()) {
                    terminal = Result.Success(requireNotNull(image))
                }
            }
            is Result.Error -> terminal = result
            Result.Loading -> Unit
        }
    }
    return terminal ?: Result.Error(uploadError(502, "Image upload was not acknowledged"))
}

private class UploadRejected(val status: Int) : Exception()

@Serializable
private data class ImagePosition(val position: Int)
