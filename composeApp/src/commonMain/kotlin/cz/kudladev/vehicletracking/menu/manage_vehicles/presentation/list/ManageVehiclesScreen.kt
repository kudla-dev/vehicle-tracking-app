package cz.kudladev.vehicletracking.menu.manage_vehicles.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cz.kudladev.vehicletracking.core.domain.models.Vehicle
import cz.kudladev.vehicletracking.core.presentation.components.basics.BackButton
import cz.kudladev.vehicletracking.core.presentation.components.basics.LargeTopBar
import cz.kudladev.vehicletracking.core.presentation.components.vehicle.VehicleGridItem
import cz.kudladev.vehicletracking.core.presentation.components.vehicle.VehicleHorizontalItem
import cz.kudladev.vehicletracking.vehicle_list.VehicleListView
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ManageVehiclesScreenRoot(
    viewModel: ManageVehiclesScreenViewModel = koinViewModel(),
    paddingValues: PaddingValues,
    onBack: () -> Unit,
    onCreate: () -> Unit,
    onEdit: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val vehicles = viewModel.vehicles.collectAsLazyPagingItems()

    ManageVehiclesScreen(
        state = state,
        onAction = viewModel::onAction,
        paddingValues = paddingValues,
        onBack = onBack,
        vehicles = vehicles,
        onCreate = onCreate,
        onEdit = onEdit,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageVehiclesScreen(
    state: ManageVehiclesScreenState,
    onAction: (ManageVehiclesScreenAction) -> Unit,
    paddingValues: PaddingValues,
    onBack: () -> Unit,
    vehicles: LazyPagingItems<Vehicle>,
    onCreate: () -> Unit,
    onEdit: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .padding(bottom = paddingValues.calculateBottomPadding())
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopBar(
                title = {
                    Text(
                        text = "Manage Vehicles"
                    )
                },
                navigationIcon = {
                    BackButton(
                        onClick = {}
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                        )
                    }
                    IconButton(
                        onClick = {
                        }
                    ) {
                        Icon(
                            Icons.Outlined.ViewAgenda,
                            contentDescription = "Change view",
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onCreate()
                }
            ){
                Icon(
                    imageVector = Icons.TwoTone.Add,
                    contentDescription = "Add vehicle",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    ) { innerPadding ->
        val combinedPadding = PaddingValues(
            bottom = paddingValues.calculateBottomPadding() + 16.dp,
            top = innerPadding.calculateTopPadding() + 16.dp,
            start = innerPadding.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
            end = innerPadding.calculateEndPadding(LocalLayoutDirection.current) + 16.dp,
        )
        when (vehicles.loadState.refresh) {
            is LoadState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(combinedPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "Loading vehicles...",
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }

            is LoadState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(combinedPadding),
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
                        text = "${(vehicles.loadState.refresh as LoadState.Error).error.message}",
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 3,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = combinedPadding,
                    columns = when (state.selectedView) {
                        VehicleListView.Grid -> GridCells.Adaptive(150.dp)
                        VehicleListView.List -> GridCells.Fixed(1)
                    },
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(vehicles.itemCount) { index ->
                        val vehicle = vehicles[index]
                        if (vehicle != null) {
                            when (state.selectedView) {
                                VehicleListView.Grid -> {
                                    VehicleGridItem(
                                        modifier = Modifier
                                            .heightIn(min = 225.dp, max = 270.dp)
                                            .fillMaxWidth(),
                                        vehicle = vehicle,
                                        onClick = { }
                                    )
                                }

                                VehicleListView.List -> {
                                    VehicleHorizontalItem(
                                        modifier = Modifier
                                            .heightIn(min = 100.dp, max = 130.dp)
                                            .fillMaxWidth(),
                                        vehicle = vehicle,
                                        onClick = { }
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
