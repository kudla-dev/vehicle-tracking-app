package cz.kudladev.vehicletracking.core.data.vehicles

import cz.kudladev.vehicletracking.core.data.vehicles.models.DriverLicenseDTO
import cz.kudladev.vehicletracking.core.data.vehicles.models.VehicleBasic
import cz.kudladev.vehicletracking.core.data.vehicles.models.VehicleImageRequest
import cz.kudladev.vehicletracking.core.data.vehicles.models.VehicleScrape
import cz.kudladev.vehicletracking.core.data.vehicles.models.getTimezoneId
import cz.kudladev.vehicletracking.core.data.vehicles.models.toCalendarMap
import cz.kudladev.vehicletracking.core.data.vehicles.models.toDomain
import cz.kudladev.vehicletracking.core.domain.vehicles.ProgressUpdate
import cz.kudladev.vehicletracking.core.domain.vehicles.VehicleRepository
import cz.kudladev.vehicletracking.menu.manage_vehicles.data.VehicleRequest
import cz.kudladev.vehicletracking.model.Vehicle
import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.Result
import cz.kudladev.vehicletracking.network.mapSuccess
import cz.kudladev.vehicletracking.network.safeCall
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone

class VehicleRepositoryImpl(
    private val httpClient: HttpClient
): VehicleRepository {
    override suspend fun get(
        search: String?,
        brandId: Long?,
        model: String?,
        year: String?,
        color: String?,
        spz: String?,
        transferableSpz: Boolean?,
        totalDistance: Int?,
        maximumDistance: Int?,
        driverLicense: String?,
        type: String?,
        subType: String?,
        place: String?,
        page: Int,
        size: Int
    ): Result<List<Vehicle>, ErrorMessage> {
        return safeCall<List<VehicleBasic>> {
            httpClient.get("/vehicles") {
                parameter("search", search)
                parameter("brandId", brandId)
                parameter("model", model)
                parameter("year", year)
                parameter("color", color)
                parameter("spz", spz)
                parameter("transferableSpz", transferableSpz)
                parameter("totalDistance", totalDistance)
                parameter("maximumDistance", maximumDistance)
                parameter("driverLicenses", driverLicense)
                parameter("place", place)
                parameter("page", page)
                parameter("size", size)
            }
        }.mapSuccess {
            it.map { vehicle->
                vehicle.toDomain()
            }
        }
    }

    override suspend fun getById(id: Long): Result<Vehicle, ErrorMessage> {
        return safeCall<VehicleBasic> {
            httpClient.get("/vehicles/$id")
        }.mapSuccess {
            it.toDomain()
        }
    }

    override suspend fun getCalendar(
        vehicleId: Long
    ): Result<Map<LocalDate, List<LocalTime>>, ErrorMessage> {
        val timezoneId = getTimezoneId()
        return safeCall<Map<String, List<String>>> {
            httpClient.get("/vehicles/$vehicleId/calendar"){
                parameter("timezone", timezoneId)
            }
        }.mapSuccess { response ->
            response.toCalendarMap()
        }
    }

    override suspend fun scrape(url: String): Result<Vehicle, ErrorMessage> {
        return safeCall<VehicleScrape> {
            httpClient.get("/vehicles/scrape") {
                parameter("url", url)
            }
        }.mapSuccess {
            it.toDomain()
        }
    }

    override suspend fun createVehicle(
        fullName: String,
        brandId: Long,
        model: String,
        year: String,
        color: String,
        spz: String,
        transferableSpz: Boolean,
        totalDistance: Int,
        maximumDistance: Int,
        driverLicense: String,
        place: String
    ): Result<Vehicle, ErrorMessage> {
        val vehicleRequest = VehicleRequest(
            fullName = fullName,
            brandId = brandId,
            model = model,
            year = year,
            color = color,
            spz = spz,
            transferableSpz = transferableSpz,
            totalDistance = totalDistance,
            maximumDistance = maximumDistance,
            driverLicense = driverLicense.split(',').map { it.trim() }.filter { it.isNotEmpty() }
                .distinct().map { DriverLicenseDTO(it) },
            place = place
        )
        return safeCall<VehicleBasic> {
            httpClient.post("/vehicles") {
                setBody(vehicleRequest)
            }
        }.mapSuccess {
            it.toDomain()
        }
    }

    override suspend fun uploadImage(
        imageData: ByteArray,
        vehicleId: Long,
        position: Int
    ): Flow<Result<ProgressUpdate, ErrorMessage>> =
        cz.kudladev.vehicletracking.core.data.images.upload.ImageUploadClient(httpClient)
            .vehicle(imageData, vehicleId, position)

    override suspend fun uploadImage(
        imageUrl: String,
        vehicleId: Long,
        position: Int
    ): Result<Vehicle, ErrorMessage> {
        val vehicleRequest = VehicleImageRequest(position)
        return safeCall<VehicleBasic> {
            httpClient.post("/vehicles/$vehicleId/images/fetch") {
                parameter("url", imageUrl)
                setBody(vehicleRequest)
            }
        }.mapSuccess {
            it.toDomain()
        }
    }
}