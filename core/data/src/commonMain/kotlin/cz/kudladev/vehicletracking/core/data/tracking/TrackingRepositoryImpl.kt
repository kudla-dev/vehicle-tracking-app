package cz.kudladev.vehicletracking.core.data.tracking

import cz.kudladev.vehicletracking.core.data.tracking.models.TrackingCreate
import cz.kudladev.vehicletracking.core.data.tracking.models.TrackingResponse
import cz.kudladev.vehicletracking.core.data.tracking.models.toDomain
import cz.kudladev.vehicletracking.core.domain.tracking.TrackingRepository
import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.Result
import cz.kudladev.vehicletracking.model.Tracking
import cz.kudladev.vehicletracking.model.TrackingState
import cz.kudladev.vehicletracking.network.mapSuccess
import cz.kudladev.vehicletracking.network.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
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

    override suspend fun getCurrentTracking(): Result<Tracking?, ErrorMessage> {
        return safeCall<TrackingResponse?> {
            httpClient.get("/trackings/user/current")
        }.mapSuccess { response ->
            response?.toDomain()
        }
    }

    override suspend fun getTrackings(
        states: List<TrackingState>,
        page: Int,
        pageSize: Int
    ): Result<List<Tracking>, ErrorMessage> {
        return safeCall<List<TrackingResponse>> {
            httpClient.get("/trackings") {
                parameter("states", states.joinToString(",") { it.state })
                parameter("page", page)
                parameter("pageSize", pageSize)
            }
        }.mapSuccess { response ->
            response.map { it.toDomain() }
        }
    }

}