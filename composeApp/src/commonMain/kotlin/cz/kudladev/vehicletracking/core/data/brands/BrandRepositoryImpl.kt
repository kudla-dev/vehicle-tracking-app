package cz.kudladev.vehicletracking.core.data.brands

import cz.kudladev.vehicletracking.core.data.models.brand.BrandDTO
import cz.kudladev.vehicletracking.core.domain.BrandRepository
import cz.kudladev.vehicletracking.core.domain.models.Brand
import cz.kudladev.vehicletracking.core.domain.models.toDomain
import cz.kudladev.vehicletracking.network.ErrorMessage
import cz.kudladev.vehicletracking.network.Result
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