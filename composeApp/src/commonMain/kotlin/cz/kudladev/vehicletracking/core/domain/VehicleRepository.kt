package cz.kudladev.vehicletracking.core.domain

import cz.kudladev.vehicletracking.core.domain.models.Vehicle
import cz.kudladev.vehicletracking.core.domain.models.VehicleScrape
import cz.kudladev.vehicletracking.network.ErrorMessage
import cz.kudladev.vehicletracking.network.Result

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
        place: String? = null,
        page: Int = 0,
        size: Int = 10
    ): Result<List<Vehicle>, ErrorMessage>

    suspend fun scrape(
        url: String,
    ): Result<VehicleScrape, ErrorMessage>

}