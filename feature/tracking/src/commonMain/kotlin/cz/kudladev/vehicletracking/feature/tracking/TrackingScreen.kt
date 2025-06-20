package cz.kudladev.vehicletracking.feature.tracking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import cz.kudladev.vehicletracking.core.ui.tracking.CurrentState
import cz.kudladev.vehicletracking.core.ui.tracking.StateHistory
import cz.kudladev.vehicletracking.core.ui.vehicle.VehicleHeader
import cz.kudladev.vehicletracking.model.Vehicle
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object Tracking

@Composable
fun TrackingScreenRoot(
    paddingValues: PaddingValues,
    viewModel: TrackingScreenViewModel = koinViewModel(),
    onVehicleClick: (Vehicle) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TrackingScreen(
        paddingValues = paddingValues,
        state = state,
        onVehicleClick = onVehicleClick,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackingScreen(
    paddingValues: PaddingValues,
    state: TrackingScreenState,
    onVehicleClick: (Vehicle) -> Unit,
    onAction: (TrackingScreenAction) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        state = pullToRefreshState,
        modifier = Modifier.fillMaxSize(),
        onRefresh = { onAction(TrackingScreenAction.Refresh) },
        isRefreshing = state.currentTracking is CurrentTrackingState.Loading,
    ){
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text("Current Tracking")
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { innerPadding ->
            val combinedPadding = PaddingValues(
                bottom = paddingValues.calculateBottomPadding(),
                top = innerPadding.calculateTopPadding(),
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current) + 16.dp,
            )
            LazyColumn(
                modifier = Modifier,
                contentPadding = combinedPadding,
            ) {
                when (state.currentTracking){
                    is CurrentTrackingState.Error -> {

                    }
                    CurrentTrackingState.Loading -> {
                        item {
                            Text(
                                text = "Loading current tracking...",
                                modifier = Modifier.padding(combinedPadding)
                            )
                        }
                    }
                    is CurrentTrackingState.Success -> {
                        if (state.currentTracking.data != null){
                            item {
                                VehicleHeader(
                                    modifier = Modifier.fillMaxWidth(),
                                    vehicle = state.currentTracking.data.vehicle,
                                    onClick = {
                                        onVehicleClick(state.currentTracking.data.vehicle)
                                    }
                                )
                                Spacer(modifier = Modifier.padding(vertical = 16.dp))
                            }
                            item {
                                CurrentState(
                                    modifier = Modifier.fillMaxWidth(),
                                    currentTracking = state.currentTracking.data.stateLogs.last(),
                                )
                                Spacer(modifier = Modifier.padding(vertical = 16.dp))
                            }
                            item {
                                StateHistory(
                                    modifier = Modifier.fillMaxWidth(),
                                    logs = state.currentTracking.data.stateLogs,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}