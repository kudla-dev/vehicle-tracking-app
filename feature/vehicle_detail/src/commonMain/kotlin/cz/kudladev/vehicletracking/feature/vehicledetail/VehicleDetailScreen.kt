package cz.kudladev.vehicletracking.feature.vehicledetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.kudladev.vehicletracking.core.designsystem.BackButton
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import cz.kudladev.vehicletracking.core.designsystem.theme.displayFontFamily
import cz.kudladev.vehicletracking.core.ui.calendar.DatePickerWithTimePicker
import cz.kudladev.vehicletracking.core.ui.calendar.DateTimePickerDefaults
import cz.kudladev.vehicletracking.core.ui.others.LoadingDialog
import cz.kudladev.vehicletracking.core.ui.vehicle.VehicleImages
import cz.kudladev.vehicletracking.core.ui.vehicle.VehicleSpecificationSection
import cz.kudladev.vehicletracking.model.Brand
import cz.kudladev.vehicletracking.model.Vehicle
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
            is TrackingCreatingState.Success -> {
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
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Vehicle Detail",
                        maxLines = 1,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis,
                        autoSize = TextAutoSize.StepBased(
                            minFontSize = 18.sp,
                            maxFontSize = 24.sp,
                            stepSize = 1.sp
                        ),
                        fontStyle = FontStyle.Italic
                    )
                },
                navigationIcon = {
                    BackButton { onBackClick() }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier
            .padding(bottom = paddingValues.calculateBottomPadding())
            .nestedScroll(scrollBehavior.nestedScrollConnection)
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
                    is VehicleLoadingState.Success -> {
                        item {
                            VehicleImages(
                                modifier = Modifier
                                    .height(300.dp),
                                images = state.vehicle.vehicle.images,
                                onImageClick = {},
                                onImagesReordered = {},
                            )
                            VehicleDetailTitle(
                                vehicle = state.vehicle
                            )
                            VehicleSpecificationSection(
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .fillMaxWidth(),
                                vehicle = state.vehicle.vehicle
                            )
                            DatePickerWithTimePicker(
                                range = true,
                                dateTimePickerDefaults = DateTimePickerDefaults(
                                    disablePastDates = true,
                                ),
                                onRangeSelected = { start, end ->
                                    onAction(VehicleDetailAction.OnStartEndDateTimeChange(start, end))
                                }
                            )
                        }
                    } else -> {

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
                    Button(
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .widthIn(300.dp, 500.dp),
                        onClick = {
                            onAction(VehicleDetailAction.CreateTracking)
                        }
                    ){
                        Text(
                            text = "Reserve"
                        )
                    }
                }
            }
        }
    }
    LoadingDialog(
        title = "Creating a new tracking...",
        isLoading = state.trackingCreatingState is TrackingCreatingState.Loading
    )
}

@Composable
private fun VehicleDetailTitle(
    vehicle: VehicleLoadingState
){
    when (vehicle) {
        is VehicleLoadingState.Success -> Text(
            text = vehicle.vehicle.fullName,
            maxLines = 1,
            softWrap = true,
            overflow = TextOverflow.Ellipsis,
            autoSize = TextAutoSize.StepBased(
                minFontSize = 18.sp,
                maxFontSize = 24.sp,
                stepSize = 1.sp
            ),
            fontFamily = displayFontFamily(),
            fontStyle = FontStyle.Italic,
        )
        else -> {
            Box(
                modifier = Modifier
                    .height(24.dp)
                    .width(150.dp)
            )
        }
    }
}

private val testVehicle = Vehicle(
    brand = Brand(
        id = 1,
        name = "Test Brand",
        logoURL = ""
    ),
    fullName = "Test Vehicle",
    color = "Redasdfasdfasdfasdfasfdasdfafsdfasdfasdfasdfasdfasdf",
    year = "2023",
    model = "Model X",
    spz = "1234 ABC",
    transferableSpz = false,
    maximumDistance = 0,
    totalDistance = 0,
    place = "Ostrava",
    driverLicense = "A1",
    images = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun VehicleDetailTitleSuccessPreview() {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    VehicleDetailTitle(
                        vehicle = VehicleLoadingState.Success(
                            vehicle = testVehicle
                        )
                    )
                },
                navigationIcon = {
                    BackButton {  }
                },
            )
        }
    ) {

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun VehicleDetailTitleLoadingPreview() {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    VehicleDetailTitle(
                        vehicle = VehicleLoadingState.Loading
                    )
                },
                navigationIcon = {
                    BackButton {  }
                },
            )
        }
    ) {

    }

}

