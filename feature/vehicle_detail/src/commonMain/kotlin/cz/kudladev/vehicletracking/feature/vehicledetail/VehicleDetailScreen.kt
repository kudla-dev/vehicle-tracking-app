package cz.kudladev.vehicletracking.feature.vehicledetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.kudladev.vehicletracking.core.designsystem.BackButton
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import cz.kudladev.vehicletracking.core.designsystem.PrimaryButton
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.core.ui.calendar.DatePickerWithTimePicker
import cz.kudladev.vehicletracking.core.ui.calendar.DatePickerWithTimePickerSkeleton
import cz.kudladev.vehicletracking.core.ui.calendar.DateTimePickerDefaults
import cz.kudladev.vehicletracking.core.ui.others.LoadingDialog
import cz.kudladev.vehicletracking.core.ui.vehicle.*
import cz.kudladev.vehicletracking.feature.vehicledetail.VehicleDetailAction.OnStartEndDateTimeChange
import cz.kudladev.vehicletracking.model.UiState
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data class VehicleDetails(val vehicleId: Int? = null)

@Composable
fun VehicleDetailRoot(
    vehicleDetailViewModel: VehicleDetailViewModel = koinViewModel(),
    paddingValues: PaddingValues,
    onCreate: (() -> Unit)? = null,
    onBack: (() -> Unit),
){
    val state by vehicleDetailViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.trackingCreatingState){
        when (state.trackingCreatingState) {
            is UiState.Success -> {
                onCreate?.invoke()
            }
            else -> {
                // No action needed for other states
            }
        }
    }

    VehicleDetailScreen(
        paddingValues = paddingValues,
        onBackClick = onBack,
        state = state,
        onAction = vehicleDetailViewModel::onAction,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailScreen(
    paddingValues: PaddingValues,
    onBackClick: () -> Unit,
    state: VehicleDetailState,
    onAction: (VehicleDetailAction) -> Unit,
) {

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        modifier = Modifier
            .padding(bottom = paddingValues.calculateBottomPadding())
    ) { innerPadding ->
        val combinedPadding = PaddingValues(
            bottom = paddingValues.calculateBottomPadding() + 16.dp,
            top = innerPadding.calculateTopPadding() + 16.dp,
            start = innerPadding.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
            end = innerPadding.calculateEndPadding(LocalLayoutDirection.current) + 16.dp,
        )
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ){
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = combinedPadding,
            ) {
                when (state.vehicle){
                    is UiState.Success -> {
                        item {
                            VehicleImages(
                                modifier = Modifier
                                    .height(300.dp),
                                images = state.vehicle.data.images,
                                onImageClick = {},
                                onImagesReordered = {},
                            )
                        }
                        item {
                            VehicleDetailTitle(
                                vehicle = state.vehicle.data
                            )
                            VehicleSpecificationSection(
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .fillMaxWidth(),
                                vehicle = state.vehicle.data
                            )
                        }
                        item {
                            when (state.calendar) {
                                is UiState.Error -> {
                                    Text(
                                        text = "Error loading calendar: ${state.calendar.message}",
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                                is UiState.Success -> {
                                    DatePickerWithTimePicker(
                                        range = true,
                                        dateTimePickerDefaults = DateTimePickerDefaults(
                                            disablePastDates = true,
                                            disabledDatesWithTimeSlot = state.calendar.data
                                        ),
                                        onRangeSelected = { start, end ->
                                            onAction(OnStartEndDateTimeChange(start, end))
                                        }
                                    )
                                }
                                else -> {
                                    DatePickerWithTimePickerSkeleton(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                    is UiState.Error -> {

                    }
                    else -> {
                        item {
                            VehicleImagesSkeleton(
                                modifier = Modifier
                                    .height(300.dp)
                            )
                        }
                        item {
                            VehicleDetailTitleSkeleton()
                        }
                        item {
                            VehicleSpecificationSectionSkeleton()
                        }
                        item {
                            DatePickerWithTimePickerSkeleton()
                        }
                    }
                }
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                AnimatedVisibility(
                    visible = state.startLocalDateTime != null && state.endLocalDateTime != null,
                    enter = fadeIn(
                        animationSpec = tween(durationMillis = 500)
                    ),
                    exit = fadeOut(
                        animationSpec = tween(durationMillis = 500)
                    )
                ){
                    PrimaryButton(
                        modifier = Modifier
                            .padding(bottom = 16.dp),
                        onClick = {
                            onAction(VehicleDetailAction.CreateTracking)
                        },
                        text = "Reserve"
                    )
                }
            }
            BackButton(
                modifier = Modifier
                    .statusBarsPadding(),
                onClick = onBackClick
            )
        }
    }
    LoadingDialog(
        title = "Creating a new tracking...",
        isLoading = state.trackingCreatingState is UiState.Loading
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun VehicleDetailTitleLoadingPreview() {
    AppTheme {
        VehicleDetailRoot(
            paddingValues = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            onCreate = {

            },
            onBack = {}
        )
    }

}

