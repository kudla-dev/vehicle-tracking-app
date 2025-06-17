package cz.kudladev.vehicletracking.core.data.brands

import cz.kudladev.vehicletracking.core.data.brands.models.BrandDTO
import cz.kudladev.vehicletracking.core.data.brands.models.toDomain
import cz.kudladev.vehicletracking.core.domain.brands.BrandRepository
import cz.kudladev.vehicletracking.model.Brand
import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.Result
import cz.kudladev.vehicletracking.network.mapSuccess
import cz.kudladev.vehicletracking.network.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class BrandRepositoryImpl(
    private val httpClient: HttpClient
): BrandRepository {
    override suspend fun getBrands(): Result<List<Brand>, ErrorMessage> {
        return safeCall<List<BrandDTO>> {
            httpClient.get("/brands")
        }.mapSuccess { it.map {
            it.toDomain()
        } }
    }
}