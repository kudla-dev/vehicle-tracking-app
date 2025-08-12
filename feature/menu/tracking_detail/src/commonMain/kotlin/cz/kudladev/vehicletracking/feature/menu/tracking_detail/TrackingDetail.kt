package cz.kudladev.vehicletracking.feature.menu.tracking_detail

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.kudladev.vehicletracking.core.designsystem.BackButton
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import cz.kudladev.vehicletracking.core.ui.image.SummaryImage
import cz.kudladev.vehicletracking.core.ui.rejectString
import cz.kudladev.vehicletracking.core.ui.submitString
import cz.kudladev.vehicletracking.core.ui.tracking.CurrentState
import cz.kudladev.vehicletracking.core.ui.tracking.StateHistory
import cz.kudladev.vehicletracking.core.ui.tracking.TrackingDetailSection
import cz.kudladev.vehicletracking.core.ui.tracking.TrackingHistory
import cz.kudladev.vehicletracking.core.ui.vehicle.VehicleHeader
import cz.kudladev.vehicletracking.model.*
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import vehicletracking.feature.menu.tracking_detail.generated.resources.Res
import vehicletracking.feature.menu.tracking_detail.generated.resources.imagesFromPickUp
import vehicletracking.feature.menu.tracking_detail.generated.resources.loadingTrackingDetails
import vehicletracking.feature.menu.tracking_detail.generated.resources.pickUp
import vehicletracking.feature.menu.tracking_detail.generated.resources.`return`
import vehicletracking.feature.menu.tracking_detail.generated.resources.trackingDetailTitle

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

    val user by viewModel.user.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    TrackingDetailScreen(
        paddingValues = paddingValues,
        state = state,
        user = user,
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
    user: User?,
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
                    Text(
                        text = stringResource(Res.string.trackingDetailTitle),
                        fontStyle = FontStyle.Italic,
                    )
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    BackButton(
                        onClick = onBack
                    )
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            user?.let {
                if (it.isAdmin()){
                    when (state.tracking) {
                        is UiState.Success -> {
                            val lastState = state.tracking.data.stateLogs.last()
                            when (lastState.state) {
                                TrackingState.PENDING -> {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        SmallExtendedFloatingActionButton(
                                            onClick = {
                                                onAction(TrackingDetailAction.RejectTracking(state.tracking.data.id))
                                            },
                                            containerColor = MaterialTheme.colorScheme.tertiary,
                                            contentColor = MaterialTheme.colorScheme.onTertiary
                                        ){
                                            Text(text = rejectString())
                                        }
                                        SmallExtendedFloatingActionButton(
                                            onClick = {
                                                onAction(TrackingDetailAction.ApproveTracking(state.tracking.data.id))
                                            }
                                        ) {
                                            Text(text = submitString())
                                        }
                                    }
                                }
                                TrackingState.APPROVED -> {
                                    SmallExtendedFloatingActionButton(
                                        onClick = {
                                            onPickUpProtocol(state.tracking.data.id, TrackingState.ACTIVE)
                                        }
                                    ) {
                                        Text(text = stringResource(Res.string.pickUp))
                                    }
                                }
                                TrackingState.ACTIVE -> {
                                    SmallExtendedFloatingActionButton(
                                        onClick = {
                                            onReturnProtocol(state.tracking.data.id, TrackingState.RETURNED)
                                        }
                                    ) {
                                        Text(text = stringResource(Res.string.`return`))
                                    }
                                }
                                else -> {}
                            }
                        }
                        else -> {}
                    }
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
                                tracking = it.data,
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
                                                stringResource(Res.string.imagesFromPickUp),
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
                            text = stringResource(Res.string.loadingTrackingDetails),
                        )
                    }
                }
            }
        }
    }



}
