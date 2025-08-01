package cz.kudladev.vehicletracking.feature.menu.tracking_detail

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.kudladev.vehicletracking.core.designsystem.BackButton
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import cz.kudladev.vehicletracking.core.ui.image.SummaryImage
import cz.kudladev.vehicletracking.core.ui.tracking.CurrentState
import cz.kudladev.vehicletracking.core.ui.tracking.StateHistory
import cz.kudladev.vehicletracking.core.ui.tracking.TimeRemaining
import cz.kudladev.vehicletracking.core.ui.tracking.TrackingDetailSection
import cz.kudladev.vehicletracking.core.ui.tracking.TrackingHistory
import cz.kudladev.vehicletracking.core.ui.vehicle.VehicleHeader
import cz.kudladev.vehicletracking.model.Tracking
import cz.kudladev.vehicletracking.model.TrackingState
import cz.kudladev.vehicletracking.model.UiState
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data class TrackingDetail(val trackingId: String)

@Composable
fun TrackingDetailRoot(
    viewModel: TrackingDetailViewModel = koinViewModel(),
    paddingValues: PaddingValues,
    onBack: () -> Unit,
    onPickUpProtocol: (String, TrackingState) -> Unit,
    onReturnProtocol: (String, TrackingState) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    TrackingDetailScreen(
        paddingValues = paddingValues,
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
        onPickUpProtocol = onPickUpProtocol,
        onReturnProtocol = onReturnProtocol,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TrackingDetailScreen(
    paddingValues: PaddingValues,
    state: TrackingDetailState,
    onAction: (TrackingDetailAction) -> Unit,
    onBack: () -> Unit,
    onPickUpProtocol: (String, TrackingState) -> Unit,
    onReturnProtocol: (String, TrackingState) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = "Tracking Detail")
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    BackButton { onBack() }
                }
            )
        },
        floatingActionButton = {
            SmallExtendedFloatingActionButton(
                onClick = {
                    when (state.tracking) {
                        is UiState.Success -> {
                            when (state.tracking.data.stateLogs.last().state) {
                                TrackingState.APPROVED -> onPickUpProtocol(state.tracking.data.id, TrackingState.ACTIVE)
                                TrackingState.ACTIVE -> onReturnProtocol(state.tracking.data.id, TrackingState.RETURNED)
                                else -> {}
                            }
                        }
                        else -> {}
                    }
                },
            ){
                when (state.tracking){
                    is UiState.Success -> {
                        when (state.tracking.data.stateLogs.last().state){
                            TrackingState.APPROVED -> {
                                Text(
                                    "Pick-Up",
                                )
                            }
                            TrackingState.ACTIVE -> {
                                Text("Return")
                            }
                            else -> {

                            }
                        }
                    }
                    else -> {}
                }
            }
        },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(bottom = paddingValues.calculateBottomPadding()),
    ) { innerPadding ->
        val combinedPadding = PaddingValues(
            bottom = paddingValues.calculateBottomPadding() + 16.dp,
            top = innerPadding.calculateTopPadding() + 16.dp,
            start = innerPadding.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
            end = innerPadding.calculateEndPadding(LocalLayoutDirection.current) + 16.dp,
        )
        Crossfade(
            targetState = state.tracking
        ){
            when (it) {
                is UiState.Error -> TODO()
                is UiState.Success<Tracking> -> {
                    LazyColumn(
                        contentPadding = combinedPadding,
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        item {
                            VehicleHeader(
                                modifier = Modifier.fillMaxWidth(),
                                vehicle = it.data.vehicle,
                                onClick = {
                                    // TODO: Navigate to vehicle detail
                                }
                            )
                        }
                        item {
                            CurrentState(
                                modifier = Modifier.fillMaxWidth(),
                                currentTracking = it.data.stateLogs.last()
                            )
                        }
                        item {
                            TrackingDetailSection(
                                modifier = Modifier.fillMaxWidth(),
                                trackingId = it.data.id,
                                startDate = it.data.startTime,
                                endDate = it.data.endTime,
                            )
                        }
                        item {
                            StateHistory(
                                modifier = Modifier.fillMaxWidth(),
                                logs = it.data.stateLogs
                            )
                        }
                        when (it.data.stateLogs.last().state) {
                            TrackingState.PENDING -> {
                                when (state.userTrackingHistory){
                                    is UiState.Error -> {

                                    }
                                    is UiState.Success<List<Tracking>> -> {
                                        item {
                                            TrackingHistory(
                                                modifier = Modifier.fillMaxWidth(),
                                                trackingHistory = state.userTrackingHistory.data,
                                                onTrackingClick = {

                                                }
                                            )
                                        }
                                    }
                                    else -> {

                                    }
                                }
                            }
                            TrackingState.ACTIVE -> {
                                when (state.tracking) {
                                    is UiState.Error -> {

                                    }
                                    is UiState.Success -> {
                                        item {
                                            Text(
                                                "Images from pick-up",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                        }
                                        items(state.tracking.data.stateLogs.last().images ?: emptyList()){
                                            SummaryImage(
                                                image = it,
                                            )
                                        }
                                    }
                                    else -> {}
                                }
                            }
                            TrackingState.RETURNED -> TODO()
                            TrackingState.REJECTED -> TODO()
                            TrackingState.FAILED -> TODO()
                            TrackingState.COMPLETED -> TODO()
                            TrackingState.ERROR -> TODO()
                            else -> {}
                        }
                    }
                }
                else -> {
                    Column {
                        Text(
                            text = "Loading tracking details...",
                        )
                    }
                }
            }
        }
    }



}
