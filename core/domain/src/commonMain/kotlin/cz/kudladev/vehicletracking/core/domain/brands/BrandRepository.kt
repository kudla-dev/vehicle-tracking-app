package cz.kudladev.vehicletracking.core.domain.brands

import cz.kudladev.vehicletracking.model.Brand
import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.Result

interface BrandRepository {

    suspend fun getBrands(): Result<List<Brand>, ErrorMessage>

}