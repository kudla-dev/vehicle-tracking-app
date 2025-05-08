package cz.kudladev.vehicletracking.core.data.vehicle

import cz.kudladev.vehicletracking.core.data.models.vehicle.VehicleBasic
import cz.kudladev.vehicletracking.core.domain.models.Vehicle
import cz.kudladev.vehicletracking.core.domain.vehicle.VehicleRepository
import cz.kudladev.vehicletracking.network.ErrorMessage
import cz.kudladev.vehicletracking.network.Result
import cz.kudladev.vehicletracking.network.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

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
        place: String?,
        page: Int,
        size: Int
    ): Result<List<VehicleBasic>, ErrorMessage> {
        return safeCall<List<VehicleBasic>>{
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
                parameter("driverLicense", driverLicense)
                parameter("place", place)
                parameter("page", page)
                parameter("size", size)
            }
        }
    }
}