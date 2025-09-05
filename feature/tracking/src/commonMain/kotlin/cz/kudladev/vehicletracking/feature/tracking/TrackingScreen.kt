package cz.kudladev.vehicletracking.feature.tracking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import cz.kudladev.vehicletracking.core.designsystem.PrimaryButton
import cz.kudladev.vehicletracking.core.designsystem.theme.Images
import cz.kudladev.vehicletracking.core.domain.SnackbarController
import cz.kudladev.vehicletracking.core.ui.backViewString
import cz.kudladev.vehicletracking.core.ui.errorString
import cz.kudladev.vehicletracking.core.ui.frontViewString
import cz.kudladev.vehicletracking.core.ui.image.SummaryImageSectionItem
import cz.kudladev.vehicletracking.core.ui.leftViewString
import cz.kudladev.vehicletracking.core.ui.others.LoadingDialog
import cz.kudladev.vehicletracking.core.ui.rightViewString
import cz.kudladev.vehicletracking.core.ui.tachometerReadingString
import cz.kudladev.vehicletracking.core.ui.tracking.CurrentState
import cz.kudladev.vehicletracking.core.ui.tracking.CurrentStateSkeleton
import cz.kudladev.vehicletracking.core.ui.tracking.StateHistory
import cz.kudladev.vehicletracking.core.ui.tracking.StateHistorySkeleton
import cz.kudladev.vehicletracking.core.ui.tracking.TimeRemaining
import cz.kudladev.vehicletracking.core.ui.tracking.TrackingDetailSection
import cz.kudladev.vehicletracking.core.ui.vehicle.VehicleHeader
import cz.kudladev.vehicletracking.core.ui.vehicle.VehicleHeaderSkeleton
import cz.kudladev.vehicletracking.model.Snackbar
import cz.kudladev.vehicletracking.model.SnackbarStyle
import cz.kudladev.vehicletracking.model.TrackingState
import cz.kudladev.vehicletracking.model.UiState
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import vehicletracking.feature.tracking.generated.resources.Res
import vehicletracking.feature.tracking.generated.resources.confirmReturn
import vehicletracking.feature.tracking.generated.resources.trackingEmpty
import vehicletracking.feature.tracking.generated.resources.trackingError
import vehicletracking.feature.tracking.generated.resources.trackingTitle

@Serializable
data object Tracking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreenRoot(
    paddingValues: PaddingValues,
    scrollBehaviour: BottomAppBarScrollBehavior,
    viewModel: TrackingScreenViewModel = koinViewModel(),
    onVehicleClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.confirmTracking) {
        if (state.confirmTracking is UiState.Success) {
            SnackbarController.sendEvent(
                Snackbar(
                    title = "Tracking confirmed",
                    snackbarStyle = SnackbarStyle.SUCCESS
                )
            )
            viewModel.onAction(TrackingScreenAction.ReturnAcknowledge)
        }
    }

    TrackingScreen(
        paddingValues = paddingValues,
        bottomAppBarScrollBehavior = scrollBehaviour,
        state = state,
        onVehicleClick = onVehicleClick,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
private fun TrackingScreen(
    paddingValues: PaddingValues,
    bottomAppBarScrollBehavior: BottomAppBarScrollBehavior,
    state: TrackingScreenState,
    onVehicleClick: (Int) -> Unit,
    onAction: (TrackingScreenAction) -> Unit
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val pullToRefreshState = rememberPullToRefreshState()

    val localHapticFeedback = LocalHapticFeedback.current


    PullToRefreshBox(
        state = pullToRefreshState,
        modifier = Modifier.fillMaxSize(),
        onRefresh = {
            onAction(TrackingScreenAction.Refresh)
            localHapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
        },
        isRefreshing = state.currentTracking is UiState.Loading,
    ){
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .nestedScroll(bottomAppBarScrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(
                            stringResource(Res.string.trackingTitle),
                            fontStyle = FontStyle.Italic
                        )
                    },
                    scrollBehavior = topAppBarScrollBehavior,
                )
            },
        ) { innerPadding ->
            val combinedPadding = PaddingValues(
                bottom = paddingValues.calculateBottomPadding() + 16.dp,
                top = innerPadding.calculateTopPadding() + 16.dp,
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current) + 16.dp,
            )
            when (state.currentTracking){
                is UiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues.calculateBottomPadding())
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = errorString(),
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = stringResource(Res.string.trackingError),
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            modifier = Modifier.widthIn(max = 300.dp),
                            text = state.currentTracking.message,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 3,
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is UiState.Success -> {
                    val data by remember {
                        mutableStateOf(state.currentTracking.data)
                    }
                    Crossfade(
                        targetState = data
                    ){
                        when(it) {
                            null -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(PaddingValues(
                                            bottom = 0.dp,
                                            start = 16.dp,
                                            end = 16.dp,
                                            top = innerPadding.calculateTopPadding()
                                        )),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        painter = painterResource(Images.NotFound),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(96.dp)
                                    )
                                    Text(
                                        text = stringResource(Res.string.trackingEmpty),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontStyle = FontStyle.Italic,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                            else -> {
                                val summaryImageTitles = listOf(
                                    frontViewString(),
                                    backViewString(),
                                    leftViewString(),
                                    rightViewString(),
                                    tachometerReadingString()
                                )

                                val nextImages by remember{
                                    mutableStateOf(
                                        data!!.stateLogs.firstOrNull() { it.state == TrackingState.RETURNED }
                                            ?.images
                                    )
                                }
                                val lastState = data!!.stateLogs.lastOrNull()
                                val activeState = data!!.stateLogs.firstOrNull { it.state == TrackingState.ACTIVE }
                                LazyColumn(
                                    modifier = Modifier,
                                    contentPadding = combinedPadding,
                                ) {
                                    item {
                                        VehicleHeader(
                                            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                                            vehicle = data!!.vehicle,
                                            onClick = {
                                                onVehicleClick(data!!.vehicle.id!!)
                                            }
                                        )
                                    }
                                    item {
                                        CurrentState(
                                            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                                            currentTracking = data!!.stateLogs.last(),
                                        )
                                    }
                                    item {
                                        AnimatedVisibility(
                                            visible = lastState?.state == TrackingState.ACTIVE
                                        ){
                                            TimeRemaining(
                                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                                startTime = data!!.startTime,
                                                endTime = data!!.endTime,
                                            )
                                        }
                                    }
                                    item {
                                        TrackingDetailSection(
                                            modifier = Modifier.fillMaxWidth(),
                                            trackingId = data!!.id,
                                            startDate = data!!.startTime,
                                            endDate = data!!.endTime,
                                        )
                                    }
                                    item {
                                        StateHistory(
                                            modifier = Modifier.fillMaxWidth(),
                                            tracking = data!!,
                                            logs = data!!.stateLogs,
                                        )
                                    }
                                    lastState?.let { state ->
                                        if (state.state == TrackingState.RETURNED || state.state == TrackingState.ACTIVE || state.state == TrackingState.COMPLETED) {
                                            itemsIndexed(summaryImageTitles) { index,page ->
                                                SummaryImageSectionItem(
                                                    beforeImage = activeState?.images?.getOrNull(index),
                                                    afterImage = nextImages?.getOrElse(index) { null },
                                                    title = page,
                                                )
                                            }
                                        }
                                        if (state.state == TrackingState.RETURNED) {
                                            item {
                                                Box(
                                                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp, bottom = 16.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    PrimaryButton(
                                                        text = stringResource(Res.string.confirmReturn),
                                                        onClick = {
                                                            onAction(TrackingScreenAction.ConfirmReturn(data!!.id, lastState.state))
                                                        },
                                                        leadingIcon = {
                                                            Icon(
                                                                imageVector = Icons.Filled.Check,
                                                                contentDescription = stringResource(Res.string.confirmReturn),
                                                            )
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding.calculateTopPadding()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        VehicleHeaderSkeleton(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                        )
                        CurrentStateSkeleton(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                        )

                        StateHistorySkeleton(
                            modifier = Modifier.fillMaxWidth(),
                            itemCount = 3
                        )

                    }
                }
            }
        }
    }

    LoadingDialog(
        title = "Confirming return",
        isLoading = state.confirmTracking is UiState.Loading,
    )
}