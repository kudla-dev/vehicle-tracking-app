package cz.kudladev.vehicletracking.core.domain.vehicles

import cz.kudladev.vehicletracking.model.Vehicle
import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.ImageWithUrl
import cz.kudladev.vehicletracking.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

interface VehicleRepository {

    suspend fun get(
        search: String? = null,
        brandId: Long? = null,
        model: String? = null,
        year: String? = null,
        color: String? = null,
        spz: String? = null,
        transferableSpz: Boolean? = null,
        totalDistance: Int? = null,
        maximumDistance: Int? = null,
        driverLicense: String? = null,
        type: String? = null,
        subType: String? = null,
        place: String? = null,
        page: Int = 0,
        size: Int = 10
    ): Result<List<Vehicle>, ErrorMessage>

    suspend fun getById(
        id: Int,
    ): Result<Vehicle, ErrorMessage>

    suspend fun getCalendar(
        vehicleId: Int,
    ): Result<Map<LocalDate, List<LocalTime>>, ErrorMessage>

    suspend fun scrape(
        url: String,
    ): Result<Vehicle, ErrorMessage>

    suspend fun createVehicle(
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
        place: String,
    ): Result<Vehicle, ErrorMessage>

    suspend fun uploadImage(
        imageData: ByteArray,
        vehicleId: Long,
        position: Int
    ): Flow<Result<ProgressUpdate, ErrorMessage>>

    suspend fun uploadImage(
        imageUrl: String,
        vehicleId: Long,
        position: Int
    ): Result<Vehicle, ErrorMessage>

}


data class ProgressUpdate(
    val byteSent: Long,
    val totalBytes: Long,
    val imageURL: ImageWithUrl? = null,
)