package cz.kudladev.vehicletracking.feature.tracking

import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import cz.kudladev.vehicletracking.core.ui.tracking.CurrentState
import cz.kudladev.vehicletracking.core.ui.tracking.StateHistory
import cz.kudladev.vehicletracking.core.ui.tracking.TimeRemaining
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
                bottom = paddingValues.calculateBottomPadding() + 16.dp,
                top = innerPadding.calculateTopPadding(),
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current) + 16.dp,
            )
            when (state.currentTracking){
                is CurrentTrackingState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(combinedPadding)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Error loading vehicles",
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            modifier = Modifier.widthIn(max = 300.dp),
                            text = "${state.currentTracking.message}",
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 3,
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                CurrentTrackingState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(combinedPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Loading current tracking...",
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                }
                is CurrentTrackingState.Success -> {
                    if (state.currentTracking.data != null){
                        LazyColumn(
                            modifier = Modifier,
                            contentPadding = combinedPadding,
                            verticalArrangement = Arrangement.spacedBy(32.dp)
                        ) {
                            item {
                                VehicleHeader(
                                    modifier = Modifier.fillMaxWidth(),
                                    vehicle = state.currentTracking.data.vehicle,
                                    onClick = {
                                        onVehicleClick(state.currentTracking.data.vehicle)
                                    }
                                )
                            }
                            item {
                                CurrentState(
                                    modifier = Modifier.fillMaxWidth(),
                                    currentTracking = state.currentTracking.data.stateLogs.last(),
                                )
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