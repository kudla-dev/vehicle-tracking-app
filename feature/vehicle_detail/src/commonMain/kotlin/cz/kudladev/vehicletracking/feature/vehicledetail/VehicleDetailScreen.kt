package cz.kudladev.vehicletracking.feature.vehicledetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.kudladev.vehicletracking.core.designsystem.BackButton
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import cz.kudladev.vehicletracking.model.Brand
import cz.kudladev.vehicletracking.model.Vehicle
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun VehicleDetailRoot(
    vehicleDetailViewModel: VehicleDetailViewModel = koinViewModel(),
    paddingValues: PaddingValues
){
    val state by vehicleDetailViewModel.state.collectAsStateWithLifecycle()
    VehicleDetailScreen(
        paddingValues = paddingValues,
        onBackClick = {},
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
    val isExpanded = scrollBehavior.state.collapsedFraction < 0.5f

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    if (isExpanded) {
                        VehicleDetailTitle(vehicle = state.vehicle)
                    } else {
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
                        )
                    }
                },
                navigationIcon = {
                    BackButton { onBackClick() }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        val combinedPadding = PaddingValues(
            bottom = paddingValues.calculateBottomPadding() + 16.dp,
            top = innerPadding.calculateTopPadding() + 16.dp,
            start = innerPadding.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
            end = innerPadding.calculateEndPadding(LocalLayoutDirection.current) + 16.dp,
        )
//        VehicleImages(
//            images = vehicleBasic.images,
//            onImageClick = {},
//            onImagesReordered = {},
//        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(combinedPadding)
        ) {
            VehicleDetailTitle(
                vehicle = state.vehicle
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = combinedPadding,
            ) {
                items(10){
                    Box(
                        modifier = Modifier
                            .height(200.dp)
                            .width(200.dp)
                    )
                }
            }
        }

    }
}

@Composable
private fun VehicleDetailTitle(
    vehicle: VehicleLoadingState
){
    when (vehicle) {
        is VehicleLoadingState.Success -> Text(
            text = "${vehicle.vehicle.model} ${vehicle.vehicle.year} ${vehicle.vehicle.color}",
            maxLines = 1,
            softWrap = true,
            overflow = TextOverflow.Ellipsis,
            autoSize = TextAutoSize.StepBased(
                minFontSize = 18.sp,
                maxFontSize = 24.sp,
                stepSize = 1.sp
            ),
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

