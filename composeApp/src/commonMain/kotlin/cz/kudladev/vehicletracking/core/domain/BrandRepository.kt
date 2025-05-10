package cz.kudladev.vehicletracking.core.domain

import cz.kudladev.vehicletracking.core.domain.models.Brand
import cz.kudladev.vehicletracking.network.ErrorMessage
import cz.kudladev.vehicletracking.network.Result

interface BrandRepository {

    suspend fun getBrands(): Result<List<Brand>, ErrorMessage>

}