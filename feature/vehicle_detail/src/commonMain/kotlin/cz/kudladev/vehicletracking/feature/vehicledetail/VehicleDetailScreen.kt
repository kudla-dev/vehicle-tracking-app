package cz.kudladev.vehicletracking.feature.vehicledetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Edit
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.kudladev.vehicletracking.core.designsystem.BackButton
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import cz.kudladev.vehicletracking.core.designsystem.PrimaryButton
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.core.domain.SnackbarController
import cz.kudladev.vehicletracking.core.domain.auth.UserStateHolder
import cz.kudladev.vehicletracking.core.ui.calendar.DatePickerWithTimePicker
import cz.kudladev.vehicletracking.core.ui.calendar.DatePickerWithTimePickerSkeleton
import cz.kudladev.vehicletracking.core.ui.calendar.DateTimePickerDefaults
import cz.kudladev.vehicletracking.core.ui.menu.MenuSection
import cz.kudladev.vehicletracking.core.ui.menu.MenuSectionItem
import cz.kudladev.vehicletracking.core.ui.others.LoadingDialog
import cz.kudladev.vehicletracking.core.ui.vehicle.*
import cz.kudladev.vehicletracking.feature.vehicledetail.VehicleDetailAction.OnStartEndDateTimeChange
import cz.kudladev.vehicletracking.model.Snackbar
import cz.kudladev.vehicletracking.model.SnackbarStyle
import cz.kudladev.vehicletracking.model.UiState
import cz.kudladev.vehicletracking.model.User
import cz.kudladev.vehicletracking.model.isAdmin
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import vehicletracking.feature.vehicle_detail.generated.resources.Res
import vehicletracking.feature.vehicle_detail.generated.resources.calendarError
import vehicletracking.feature.vehicle_detail.generated.resources.reserveAction
import vehicletracking.feature.vehicle_detail.generated.resources.trackingCreationLoading

@Serializable
data class VehicleDetails(
    val vehicleId: Int? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailRoot(
    vehicleDetailViewModel: VehicleDetailViewModel = koinViewModel(),
    bottomAppBarScrollBehavior: BottomAppBarScrollBehavior,
    paddingValues: PaddingValues,
    onCreate: (() -> Unit)? = null,
    onBack: (() -> Unit),
    userStateHolder: UserStateHolder = koinInject()
){
    val state by vehicleDetailViewModel.state.collectAsStateWithLifecycle()
    val userState by userStateHolder.user.collectAsStateWithLifecycle()

    LaunchedEffect(state.trackingCreatingState){
        when (state.trackingCreatingState) {
            is UiState.Success -> {
                SnackbarController.sendEvent(Snackbar(
                    title = "Reservation created successfully",
                    snackbarStyle = SnackbarStyle.SUCCESS
                ))
                vehicleDetailViewModel.onAction(VehicleDetailAction.ReservationAcknowledged)
                onCreate?.invoke()
            }
            is UiState.Error -> {
                SnackbarController.sendEvent(Snackbar(
                    title = (state.trackingCreatingState as UiState.Error).message,
                    snackbarStyle = SnackbarStyle.ERROR
                ))
                vehicleDetailViewModel.onAction(VehicleDetailAction.ReservationAcknowledged)
            }
            else -> {
                // No action needed for other states
            }
        }
    }

    VehicleDetailScreen(
        paddingValues = paddingValues,
        bottomAppBarScrollBehavior = bottomAppBarScrollBehavior,
        onBackClick = onBack,
        state = state,
        user = userState,
        onAction = vehicleDetailViewModel::onAction,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailScreen(
    paddingValues: PaddingValues,
    bottomAppBarScrollBehavior: BottomAppBarScrollBehavior,
    onBackClick: () -> Unit,
    state: VehicleDetailState,
    user: User?,
    onAction: (VehicleDetailAction) -> Unit,
) {

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        modifier = Modifier
            .nestedScroll(bottomAppBarScrollBehavior.nestedScrollConnection)
            .padding(bottom = paddingValues.calculateBottomPadding())
    ) { innerPadding ->
        val combinedPadding = PaddingValues(
            bottom = paddingValues.calculateBottomPadding() + 16.dp,
            top = innerPadding.calculateTopPadding() + 16.dp,
            start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
            end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
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
                                modifier = Modifier.padding(horizontal = 16.dp),
                                vehicle = state.vehicle.data
                            )
                            VehicleSpecificationSection(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                vehicle = state.vehicle.data
                            )
                        }
                        item {
                            when (state.calendar) {
                                is UiState.Error -> {
                                    Text(
                                        text = stringResource(Res.string.calendarError),
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                                is UiState.Success -> {
                                    DatePickerWithTimePicker(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        range = true,
                                        dateTimePickerDefaults = DateTimePickerDefaults(
                                            disablePastDates = true,
                                            disabledDatesWithTimeSlot = state.calendar.data,
                                            monthNames = DateTimePickerDefaults.localizedMonthNames(),
                                            dayOfWeekNames = DateTimePickerDefaults.localizedDayOfWeekNames()
                                        ),
                                        onRangeSelected = { start, end ->
                                            onAction(OnStartEndDateTimeChange(start, end))
                                        }
                                    )
                                }
                                else -> {
                                    DatePickerWithTimePickerSkeleton(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .fillMaxWidth()
                                    )
                                }
                            }
                        }
                        user?.let { user ->
                            item {
                                AnimatedVisibility(
                                    visible = user.isAdmin()
                                ){
                                    MenuSection(
                                        "Admin Options"
                                    ) {
                                        MenuSectionItem(
                                            icon = Icons.TwoTone.Edit,
                                            title = "Edit Vehicle",
                                            onClick = {},
                                            isLast = true
                                        )
                                    }
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
                                    .padding(horizontal = 16.dp)
                            )
                        }
                        item {
                            VehicleDetailTitleSkeleton(
                                modifier = Modifier
                                    .padding(16.dp)
                            )
                        }
                        item {
                            VehicleSpecificationSectionSkeleton(
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        item {
                            DatePickerWithTimePickerSkeleton(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                            )
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
                        text = stringResource(Res.string.reserveAction)
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
        title = stringResource(Res.string.trackingCreationLoading),
        isLoading = state.trackingCreatingState is UiState.Loading
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun VehicleDetailTitleLoadingPreview() {
    AppTheme {
        VehicleDetailScreen(
            bottomAppBarScrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior(),
            paddingValues = PaddingValues(),
            onBackClick = {},
            state = VehicleDetailState(),
            onAction = {},
            user = null
        )
    }

}

