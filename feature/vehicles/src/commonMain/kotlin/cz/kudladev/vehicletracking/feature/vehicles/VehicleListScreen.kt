package cz.kudladev.vehicletracking.feature.vehicles

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cz.kudladev.vehicletracking.core.designsystem.IconFloatingActionButton
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import cz.kudladev.vehicletracking.core.domain.auth.UserStateHolder
import cz.kudladev.vehicletracking.core.ui.errorString
import cz.kudladev.vehicletracking.core.ui.searchString
import cz.kudladev.vehicletracking.core.ui.vehicle.VehicleGridItem
import cz.kudladev.vehicletracking.core.ui.vehicle.VehicleGridItemSkeleton
import cz.kudladev.vehicletracking.core.ui.vehicle.VehicleHorizontalItem
import cz.kudladev.vehicletracking.core.ui.vehicle.VehicleHorizontalItemSkeleton
import cz.kudladev.vehicletracking.model.User
import cz.kudladev.vehicletracking.model.Vehicle
import cz.kudladev.vehicletracking.model.isAdmin
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import vehicletracking.feature.vehicles.generated.resources.Res
import vehicletracking.feature.vehicles.generated.resources.addVehicle
import vehicletracking.feature.vehicles.generated.resources.changeView
import vehicletracking.feature.vehicles.generated.resources.vehicleEmpty
import vehicletracking.feature.vehicles.generated.resources.vehicleEmptyDescription
import vehicletracking.feature.vehicles.generated.resources.vehicleError
import vehicletracking.feature.vehicles.generated.resources.vehiclesManageTitle
import vehicletracking.feature.vehicles.generated.resources.vehiclesSearchResults
import vehicletracking.feature.vehicles.generated.resources.vehiclesTitle

@Serializable
data class VehicleList(
    val searchQuery: String? = null,
    val create: Boolean = false,
)

@Composable
fun VehicleListScreenRoot(
    userState: UserStateHolder = koinInject(),
    vehicleListViewModel: VehicleListViewModel = koinViewModel(),
    paddingValues: PaddingValues,
    onSearch: (String) -> Unit,
    searchQuery: String? = null,
    onVehicleClick: (Int) -> Unit,
    onCreate: (() -> Unit)? = null,
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
        onVehicleClick = onVehicleClick,
        onCreate = onCreate,
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
    searchQuery: String?,
    onVehicleClick: (Int) -> Unit,
    onCreate: (() -> Unit)? = null,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = Modifier
            .fillMaxSize(),
        state = pullToRefreshState,
        isRefreshing = vehicles.loadState.refresh is LoadState.Loading,
        onRefresh = { vehicles.refresh() }
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(
                            text = when {
                                searchQuery != null -> stringResource(Res.string.vehiclesSearchResults, searchQuery)
                                onCreate != null -> stringResource(Res.string.vehiclesManageTitle)
                                else -> stringResource(Res.string.vehiclesTitle)
                            },
                            fontStyle = FontStyle.Italic
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
                                contentDescription = searchString(),
                                tint = MaterialTheme.colorScheme.onBackground
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
                                contentDescription = stringResource(Res.string.changeView),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            floatingActionButton = {
                if (onCreate != null && user?.isAdmin() == true) {
                    IconFloatingActionButton(
                        onClick = onCreate,
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = stringResource(Res.string.addVehicle),
                            )
                        }
                    )
                }
            },
        ) { innerPadding ->
            val combinedPadding = PaddingValues(
                bottom = paddingValues.calculateBottomPadding() + 16.dp,
                top = innerPadding.calculateTopPadding() + 16.dp,
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current) + 16.dp,
            )
            Crossfade(
                targetState = vehicles.loadState.refresh
            ){
                when (vehicles.loadState.refresh) {
                    is LoadState.Loading -> {
                        LazyVerticalGrid(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = combinedPadding,
                            columns = when(state.selectedView){
                                VehicleListView.Grid -> GridCells.Adaptive(150.dp)
                                VehicleListView.List -> GridCells.Fixed(1)
                            },
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(6){
                                when (state.selectedView) {
                                    VehicleListView.Grid -> {
                                        VehicleGridItemSkeleton(
                                            modifier = Modifier
                                                .animateItem()
                                                .heightIn(min = 225.dp, max = 270.dp)
                                                .fillMaxWidth()
                                        )
                                    }
                                    VehicleListView.List -> {
                                        VehicleHorizontalItemSkeleton(
                                            modifier = Modifier
                                                .animateItem()
                                                .heightIn(min = 100.dp, max = 130.dp)
                                                .fillMaxWidth()
                                        )
                                    }
                                }
                            }
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
                                contentDescription = errorString(),
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                text = stringResource(Res.string.vehicleError),
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
                        when (vehicles.itemCount == 0) {
                            true -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(combinedPadding),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = stringResource(Res.string.vehicleEmpty),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Text(
                                        text = stringResource(Res.string.vehicleEmptyDescription),
                                        modifier = Modifier.padding(top = 8.dp),
                                        style = MaterialTheme.typography.titleSmall,
                                    )
                                }
                            }
                            false -> {
                                LazyVerticalGrid(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = combinedPadding,
                                    columns = when(state.selectedView){
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
                                                            .animateItem()
                                                            .heightIn(min = 225.dp, max = 270.dp)
                                                            .fillMaxWidth(),
                                                        vehicle = vehicle,
                                                        onClick = {
                                                            onVehicleClick(vehicle.id ?: 0)
                                                        }
                                                    )
                                                }
                                                VehicleListView.List -> {
                                                    VehicleHorizontalItem(
                                                        modifier = Modifier
                                                            .animateItem()
                                                            .heightIn(min = 100.dp, max = 130.dp)
                                                            .fillMaxWidth(),
                                                        vehicle = vehicle,
                                                        onClick = {
                                                            onVehicleClick(vehicle.id ?: 0)
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
            }
        }
    }
}