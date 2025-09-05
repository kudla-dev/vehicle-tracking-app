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
import androidx.compose.ui.text.style.TextAlign
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
import cz.kudladev.vehicletracking.model.User
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import vehicletracking.feature.history.generated.resources.*

@Serializable
data object History

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreenRoot(
    paddingValues: PaddingValues,
    bottomAppBarScrollBehavior: BottomAppBarScrollBehavior,
    viewModel: HistoryScreenViewModel = koinViewModel()
){
    val trackings = viewModel.trackings.collectAsLazyPagingItems()

    val user by viewModel.user.collectAsStateWithLifecycle()

    LaunchedEffect(trackings) {
        println("Trackings: $trackings")
    }

    HistoryScreen(
        paddingValues = paddingValues,
        bottomAppBarScrollBehavior = bottomAppBarScrollBehavior,
        onAction = viewModel::onAction,
        trackings = trackings,
        user = user
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    paddingValues: PaddingValues,
    bottomAppBarScrollBehavior: BottomAppBarScrollBehavior,
    onAction: (HistoryScreenAction) -> Unit,
    trackings: LazyPagingItems<Tracking>,
    user: User? = null
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

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
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .nestedScroll(bottomAppBarScrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(
                            stringResource(Res.string.historyTitle),
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
            val columnPadding = PaddingValues(
                top = combinedPadding.calculateTopPadding(),
                start = combinedPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = combinedPadding.calculateEndPadding(LocalLayoutDirection.current)
            )
            Crossfade(
                targetState = trackings.loadState.refresh
            ){
                when (it) {
                    is LoadState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(columnPadding),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(Res.string.historyError, it.error.message?: stringResource(Res.string.historyEmpty)),
                                modifier = Modifier.padding(combinedPadding)
                            )
                        }
                    }
                    LoadState.Loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(columnPadding),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            repeat(6){
                                TrackingItemSkeleton()
                            }
                        }
                    }
                    else -> {
                        when (trackings.itemCount == 0) {
                            true -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(columnPadding),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        painter = painterResource(Images.NotFound),
                                        contentDescription = stringResource(Res.string.historyEmpty),
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Text(
                                        text = stringResource(Res.string.historyEmpty),
                                        modifier = Modifier.padding(top = 8.dp),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontStyle = FontStyle.Italic
                                    )
                                    Text(
                                        text = stringResource(Res.string.historyEmptyDescription),
                                        modifier = Modifier.padding(top = 4.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                            false -> {
                                LazyColumn(
                                    contentPadding = combinedPadding,
                                    modifier = Modifier
                                        .fillMaxSize(),
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