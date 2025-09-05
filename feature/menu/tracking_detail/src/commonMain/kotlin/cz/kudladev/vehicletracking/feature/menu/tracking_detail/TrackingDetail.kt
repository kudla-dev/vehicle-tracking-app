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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.kudladev.vehicletracking.core.designsystem.BackButton
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import cz.kudladev.vehicletracking.core.designsystem.PrimaryButton
import cz.kudladev.vehicletracking.core.designsystem.SecondaryButton
import cz.kudladev.vehicletracking.core.ui.*
import cz.kudladev.vehicletracking.core.ui.image.SummaryImageSectionItem
import cz.kudladev.vehicletracking.core.ui.others.LoadingDialog
import cz.kudladev.vehicletracking.core.ui.tracking.CurrentState
import cz.kudladev.vehicletracking.core.ui.tracking.StateHistory
import cz.kudladev.vehicletracking.core.ui.tracking.TrackingDetailSection
import cz.kudladev.vehicletracking.core.ui.tracking.TrackingHistory
import cz.kudladev.vehicletracking.core.ui.user.UserCard
import cz.kudladev.vehicletracking.core.ui.vehicle.VehicleHeader
import cz.kudladev.vehicletracking.model.*
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import vehicletracking.feature.menu.tracking_detail.generated.resources.*
import kotlin.math.max

@Serializable
data class TrackingDetail(val trackingId: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingDetailRoot(
    viewModel: TrackingDetailViewModel = koinViewModel(),
    paddingValues: PaddingValues,
    bottomAppBarScrollBehavior: BottomAppBarScrollBehavior,
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
        bottomAppBarScrollBehavior = bottomAppBarScrollBehavior,
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
    bottomAppBarScrollBehavior: BottomAppBarScrollBehavior,
    state: TrackingDetailState,
    user: User?,
    onAction: (TrackingDetailAction) -> Unit,
    onBack: () -> Unit,
    onPickUpProtocol: (String, TrackingState) -> Unit,
    onReturnProtocol: (String, TrackingState) -> Unit,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.trackingDetailTitle),
                        fontStyle = FontStyle.Italic,
                    )
                },
                scrollBehavior = topAppBarScrollBehavior,
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
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        SecondaryButton(
                                            modifier = Modifier.weight(1f),
                                            onClick = {
                                                onAction(TrackingDetailAction.RejectTracking(state.tracking.data.id))
                                            },
                                            text = rejectString()
                                        )
                                        PrimaryButton(
                                            modifier = Modifier.weight(1f),
                                            onClick = {
                                                onAction(TrackingDetailAction.ApproveTracking(state.tracking.data.id))
                                            },
                                            text = submitString()
                                        )
                                    }
                                }
                                TrackingState.APPROVED -> {
                                    PrimaryButton(
                                        onClick = {
                                            onPickUpProtocol(state.tracking.data.id, TrackingState.ACTIVE)
                                        },
                                        text = stringResource(Res.string.pickUp)
                                    )
                                }
                                TrackingState.ACTIVE -> {
                                    PrimaryButton(
                                        onClick = {
                                            onReturnProtocol(state.tracking.data.id, TrackingState.RETURNED)
                                        },
                                        text = stringResource(Res.string.`return`)
                                    )
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
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            .nestedScroll(bottomAppBarScrollBehavior.nestedScrollConnection)
            .padding(bottom = paddingValues.calculateBottomPadding()),
    ) { innerPadding ->
        val combinedPadding = PaddingValues(
            bottom = paddingValues.calculateBottomPadding() + 16.dp,
            top = innerPadding.calculateTopPadding() + 16.dp,
            start = innerPadding.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
            end = innerPadding.calculateEndPadding(LocalLayoutDirection.current) + 16.dp,
        )
        val summaryImageTitles = listOf(
            frontViewString(),
            backViewString(),
            leftViewString(),
            rightViewString(),
            tachometerReadingString()
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
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)

                            ) {
                                Text(
                                    text = "Vehicle",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    fontStyle = FontStyle.Italic,
                                )
                                VehicleHeader(
                                    modifier = Modifier.fillMaxWidth(),
                                    vehicle = it.data.vehicle,
                                    onClick = {
                                        // TODO: Navigate to vehicle detail
                                    }
                                )
                            }
                        }
                        item {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Requester",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    fontStyle = FontStyle.Italic,
                                )
                                UserCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    user = it.data.user,
                                    onClick = {

                                    }
                                )
                            }
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
                                        items(state.activeImage) { image ->
                                            SummaryImageSectionItem(
                                                beforeImage = image,
                                                title = summaryImageTitles.getOrNull(state.activeImage.indexOf(image)) ?: "",
                                                onRetryBeforeImage = {
//                                                    onAction(TrackingDetailAction.RetryImageUpload(it.data.id, image.id))
                                                }
                                            )
                                        }
                                    }
                                    else -> {}
                                }
                            }
                            TrackingState.RETURNED -> {
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
                                        items(max(state.activeImage.size, state.returnImage.size)) { index ->
                                            SummaryImageSectionItem(
                                                beforeImage = state.activeImage.getOrNull(index),
                                                afterImage = state.returnImage.getOrNull(index),
                                                title = summaryImageTitles.getOrNull(index) ?: "",
                                                onRetryBeforeImage = {

                                                },
                                                onRetryAfterImage = {

                                                }
                                            )
                                        }
                                    }
                                    else -> {}
                                }
                            }
                            TrackingState.REJECTED -> {

                            }
                            TrackingState.FAILED -> {

                            }
                            TrackingState.COMPLETED -> {

                            }
                            TrackingState.ERROR -> {

                            }
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

    LoadingDialog(
        title = "Please wait",
        isLoading = state.updatedTracking is UiState.Loading
    )
}
