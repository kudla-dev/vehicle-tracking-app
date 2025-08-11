package cz.kudladev.vehicletracking.feature.history

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import cz.kudladev.vehicletracking.core.designsystem.theme.Images
import cz.kudladev.vehicletracking.core.ui.tracking.TrackingItem
import cz.kudladev.vehicletracking.core.ui.tracking.TrackingItemSkeleton
import cz.kudladev.vehicletracking.core.ui.user.UserDistanceProgress
import cz.kudladev.vehicletracking.model.Tracking
import cz.kudladev.vehicletracking.model.UiState
import cz.kudladev.vehicletracking.model.User
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object History

@Composable
fun HistoryScreenRoot(
    paddingValues: PaddingValues,
    viewModel: HistoryScreenViewModel = koinViewModel()
){
    val trackings = viewModel.trackings.collectAsLazyPagingItems()

    val user by viewModel.user.collectAsStateWithLifecycle()

    LaunchedEffect(trackings) {
        println("Trackings: $trackings")
    }

    HistoryScreen(
        paddingValues = paddingValues,
        onAction = viewModel::onAction,
        trackings = trackings,
        user = user
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    paddingValues: PaddingValues,
    onAction: (HistoryScreenAction) -> Unit,
    trackings: LazyPagingItems<Tracking>,
    user: User? = null
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        state = pullToRefreshState,
        modifier = Modifier.fillMaxSize(),
        isRefreshing = trackings.loadState.refresh is LoadState.Loading,
        onRefresh = { trackings.refresh() }
    ){
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(
                            "History",
                            fontStyle = FontStyle.Italic

                        )
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { innerPadding ->
            val combinedPadding = PaddingValues(
                bottom = paddingValues.calculateBottomPadding() + 16.dp,
                top = innerPadding.calculateTopPadding() + 16.dp,
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current) + 16.dp,
            )
            Crossfade(
                targetState = trackings.loadState.refresh
            ){
                when (it) {
                    is LoadState.Error -> {
                        Text(
                            text = "Error loading trackings: ${it.error}",
                            modifier = Modifier.padding(combinedPadding)
                        )
                    }
                    LoadState.Loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(combinedPadding),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            repeat(6){
                                TrackingItemSkeleton(
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }
                    else -> {
                        when (trackings.itemCount == 0) {
                            true -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(combinedPadding),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        painter = painterResource(Images.NotFound),
                                        contentDescription = "No trackings found",
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Text(
                                        text = "No trackings found",
                                        modifier = Modifier.padding(top = 8.dp),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                            }
                            false -> {
                                LazyColumn(
                                    contentPadding = combinedPadding,
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    user?.let { user ->
                                        stickyHeader {
                                            UserDistanceProgress(
                                                modifier = Modifier.fillMaxWidth(),
                                                distance = user.overallDistance,
                                                maximumDistance = user.maximumDistance,
                                            )
                                        }
                                    }

                                    items(trackings.itemCount) { tracking ->
                                        val item = trackings[tracking]
                                        if (item != null) {
                                            TrackingItem(
                                                tracking = item,
                                                onClick = {

                                                },
                                                modifier = Modifier.padding(horizontal = 8.dp)
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