package cz.kudladev.vehicletracking.core.data.tracking

import cz.kudladev.vehicletracking.core.data.tracking.models.TrackingCreate
import cz.kudladev.vehicletracking.core.data.tracking.models.TrackingLogRequest
import cz.kudladev.vehicletracking.core.data.tracking.models.TrackingResponse
import cz.kudladev.vehicletracking.core.data.tracking.models.toDomain
import cz.kudladev.vehicletracking.core.domain.tracking.TrackingRepository
import cz.kudladev.vehicletracking.core.domain.vehicles.ProgressUpdate
import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.ImageWithUrl
import cz.kudladev.vehicletracking.model.Result
import cz.kudladev.vehicletracking.model.Tracking
import cz.kudladev.vehicletracking.model.TrackingState
import cz.kudladev.vehicletracking.network.mapSuccess
import cz.kudladev.vehicletracking.network.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.headers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class TrackingRepositoryImpl(
    private val httpClient: HttpClient
): TrackingRepository {

    override suspend fun createTracking(
        vehicleId: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Result<Tracking, ErrorMessage> {
        val request = TrackingCreate(
            vehicleId = vehicleId,
            startTime = startTime.toInstant(TimeZone.currentSystemDefault()).toString(),
            endTime = endTime.toInstant(TimeZone.currentSystemDefault()).toString()
        )
        return safeCall<TrackingResponse>{
            httpClient.post("/trackings"){
                setBody(request)
            }
        }.mapSuccess { response ->
            response.toDomain()
        }
    }

    override suspend fun updateTracking(
        trackingId: String,
        state: TrackingState,
        message: String?,
        tachometer: Int?
    ): Result<Tracking, ErrorMessage> {
        val tracking = TrackingLogRequest(
            state = state.state,
            message = message,
            tachometer = tachometer
        )
        return safeCall<TrackingResponse>{
            httpClient.post("/trackings/$trackingId/logs") {
                setBody(tracking)
            }
        }.mapSuccess { response ->
            response.toDomain()
        }
    }

    override suspend fun getCountByState(state: TrackingState): Result<Int, ErrorMessage> {
        return safeCall<Int> {
            httpClient.get("/trackings/${state.state}/count")
        }.mapSuccess { response ->
            response
        }
    }

    override suspend fun uploadImage(
        imageData: ByteArray,
        trackingId: String,
        position: Int,
        state: String
    ): Flow<Result<ProgressUpdate, ErrorMessage>> = channelFlow {
        safeCall<ImageWithUrl> {
            httpClient.submitFormWithBinaryData(
                url = "/trackings/$trackingId/logs/$state/images?position=$position",
                formData = formData {
                    append("file", imageData, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"image.png\"")
                    })
                }
            ) {
                onUpload { bytesSentTotal, totalBytes ->
                    if ((totalBytes ?: 0L) > 0L) {
                        send(Result.Success(ProgressUpdate(bytesSentTotal, totalBytes ?: 0L)))
                    }
                }
            }
        }.mapSuccess { response ->
            send(Result.Success( data = ProgressUpdate(
                byteSent = 0,
                totalBytes = 0,
                imageURL = response
            )))
        }
    }

    override suspend fun getCurrentTracking(): Result<Tracking?, ErrorMessage> {
        return safeCall<TrackingResponse?> {
            httpClient.get("/trackings/user/current")
        }.mapSuccess { response ->
            response?.toDomain()
        }
    }

    override suspend fun getTrackings(
        states: List<TrackingState>,
        userId: String?,
        vehicleId: Long?,
        page: Int,
        pageSize: Int
    ): Result<List<Tracking>, ErrorMessage> {
        return safeCall<List<TrackingResponse>> {
            httpClient.get("/trackings") {
                parameter("states", states.joinToString(",") { it.state })
                userId?.let { parameter("userId", it) }
                vehicleId?.let { parameter("vehicleId", it) }
                parameter("page", page)
                parameter("pageSize", pageSize)
            }
        }.mapSuccess { response ->
            response.map { it.toDomain() }
        }
    }

    override suspend fun getTracking(id: String): Result<Tracking, ErrorMessage> {
        return safeCall<TrackingResponse> {
            httpClient.get("/trackings/$id")
        }.mapSuccess { response ->
            response.toDomain()
        }
    }

    override suspend fun getUserTrackingHistory(
        userId: String,
        page: Int,
        pageSize: Int,
        includeActive: Boolean
    ): Result<List<Tracking>, ErrorMessage> {
        return safeCall<List<TrackingResponse>> {
            httpClient.get("/users/$userId/tracking") {
                parameter("page", page)
                parameter("pageSize", pageSize)
                parameter("includeActive", includeActive)
            }
        }.mapSuccess { response ->
            response.map { it.toDomain() }
        }
    }

}