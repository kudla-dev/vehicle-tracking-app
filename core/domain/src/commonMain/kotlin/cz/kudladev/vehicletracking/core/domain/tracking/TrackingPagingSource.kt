package cz.kudladev.vehicletracking.core.domain.tracking

import androidx.paging.PagingSource
import app.cash.paging.PagingState
import cz.kudladev.vehicletracking.model.Tracking
import cz.kudladev.vehicletracking.model.TrackingState
import cz.kudladev.vehicletracking.model.onError
import cz.kudladev.vehicletracking.model.onSuccess

class TrackingPagingSource(
    private val trackingRepository: TrackingRepository,
    private val states: List<TrackingState>,
    private val userId: String? = null,
    private val vehicleId: Long? = null
): PagingSource<Int, Tracking>() {

    override fun getRefreshKey(state: PagingState<Int, Tracking>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Tracking> {
        val nextPage = params.key ?: 0

        trackingRepository
            .getTrackings(
                states = states,
                userId = userId,
                vehicleId = vehicleId,
                page = nextPage,
                pageSize = params.loadSize
            )
            .onSuccess { response ->
                return LoadResult.Page(
                    data = response,
                    prevKey = if (nextPage <= 0) null else nextPage - 1,
                    nextKey = if (response.isEmpty()) null else nextPage + 1
                )
            }
            .onError { error ->
                return LoadResult.Error(Throwable(error.message))
            }
        return LoadResult.Error(Throwable("Unknown error occurred"))
    }
}