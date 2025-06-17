package cz.kudladev.vehicletracking.core.domain.vehicles

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cz.kudladev.vehicletracking.model.Vehicle
import cz.kudladev.vehicletracking.model.onError
import cz.kudladev.vehicletracking.model.onSuccess

class VehiclePagingSource(
    private val vehicleRepository: VehicleRepository,
    private val search: String? = null,
    private val brandId: Long? = null,
    private val model: String? = null,
    private val year: String? = null,
    private val color: String? = null,
    private val spz: String? = null,
    private val transferableSpz: Boolean? = null,
    private val totalDistance: Int? = null,
    private val maximumDistance: Int? = null,
    private val driverLicense: String? = null,
    private val place: String? = null,
): PagingSource<Int, Vehicle>() {

    override fun getRefreshKey(state: PagingState<Int, Vehicle>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Vehicle> {
        val nextPage = params.key ?: 1

        vehicleRepository
            .get(
                search = search,
                brandId = brandId,
                model = model,
                year = year,
                color = color,
                spz = spz,
                transferableSpz = transferableSpz,
                totalDistance = totalDistance,
                maximumDistance = maximumDistance,
                driverLicense = driverLicense,
                place = place,
                page = nextPage,
                size = params.loadSize
            )
            .onSuccess { response ->
                return LoadResult.Page(
                    data = response,
                    prevKey = if (nextPage == 1) null else nextPage - 1,
                    nextKey = if (response.isEmpty()) null else nextPage + 1
                )
            }
            .onError { error ->
                return LoadResult.Error(
                    Throwable(error.message)
                )
            }
        return LoadResult.Error(
            Throwable("Unknown error")
        )
    }

}