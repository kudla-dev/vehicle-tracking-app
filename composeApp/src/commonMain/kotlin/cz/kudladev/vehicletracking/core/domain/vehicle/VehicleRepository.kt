package cz.kudladev.vehicletracking.core.domain.vehicle

import cz.kudladev.vehicletracking.core.data.models.vehicle.VehicleBasic
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
    ): Result<List<VehicleBasic>, ErrorMessage>

}