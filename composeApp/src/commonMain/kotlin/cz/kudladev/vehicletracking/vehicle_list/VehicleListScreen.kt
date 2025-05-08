package cz.kudladev.vehicletracking.vehicle_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cz.kudladev.vehicletracking.app.nested.Search
import cz.kudladev.vehicletracking.auth.domain.User
import cz.kudladev.vehicletracking.auth.domain.UserStateHolder
import cz.kudladev.vehicletracking.core.domain.models.Vehicle
import cz.kudladev.vehicletracking.core.presentation.components.basics.TopAppBar
import cz.kudladev.vehicletracking.core.presentation.components.vehicle.VehicleGridItem
import cz.kudladev.vehicletracking.core.presentation.components.vehicle.VehicleHorizontalItem
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun VehicleListScreenRoot(
    userState: UserStateHolder = koinInject(),
    vehicleListViewModel: VehicleListViewModel = koinViewModel(),
    paddingValues: PaddingValues,
    onSearch: (String) -> Unit,
    searchQuery: String? = null,
) {
    val user = userState.user.collectAsStateWithLifecycle()
    val vehicles = vehicleListViewModel.vehicles.collectAsLazyPagingItems()
    val state = vehicleListViewModel.state.collectAsStateWithLifecycle()

    VehicleListScreen(
        state = state.value,
        onAction = vehicleListViewModel::onAction,
        paddingValues = paddingValues,
        vehicles = vehicles,
        user = user.value,
        onSearch = onSearch,
        searchQuery = searchQuery,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VehicleListScreen(
    state: VehicleListState,
    onAction: (VehicleListAction) -> Unit,
    paddingValues: PaddingValues,
    vehicles: LazyPagingItems<Vehicle>,
    user: User?,
    onSearch: (String) -> Unit,
    searchQuery: String?
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = searchQuery?.let {
                            "Search results for \"$it\""
                        } ?: "Vehicles",
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            onSearch("vehicle")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                        )
                    }
                    IconButton(
                        onClick = {
                            onAction(VehicleListAction.ToggleView)
                        }
                    ) {
                        Icon(
                            imageVector = when(state.selectedView){
                                VehicleListView.Grid -> Icons.Outlined.GridView
                                VehicleListView.List -> Icons.Outlined.ViewAgenda
                            },
                            contentDescription = "Change view",
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = paddingValues.calculateBottomPadding() + 16.dp,
                top = innerPadding.calculateTopPadding() + 16.dp,
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current) + 16.dp,
            ),
            columns = when(state.selectedView){
                VehicleListView.Grid -> GridCells.Adaptive(150.dp)
                VehicleListView.List -> GridCells.Fixed(1)
            },
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            when (vehicles.loadState.refresh) {
                is LoadState.Loading -> {
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                    ) {
                        Text(text = "Loading vehicles...")
                    }
                }
                is LoadState.Error -> {
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                    ) {
                        Text(text = "Error loading vehicles ")
                    }
                }
                else -> {
                    items(vehicles.itemCount) { index ->
                        val vehicle = vehicles[index]
                        if (vehicle != null) {
                            when(state.selectedView){
                                VehicleListView.Grid -> {
                                    VehicleGridItem(
                                        modifier = Modifier
                                            .heightIn(min = 275.dp, max = 350.dp)
                                            .fillMaxWidth(),
                                        vehicle = vehicle,
                                        onClick = {

                                        }
                                    )
                                }
                                VehicleListView.List -> {
                                    VehicleHorizontalItem(
                                        modifier = Modifier
                                            .heightIn(min = 120.dp, max = 200.dp)
                                            .fillMaxWidth(),
                                        vehicle = vehicle,
                                        onClick = {

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