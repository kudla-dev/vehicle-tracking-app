package cz.kudladev.vehicletracking.feature.menu.manage_trackings

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import cz.kudladev.vehicletracking.core.designsystem.BackButton
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import cz.kudladev.vehicletracking.core.designsystem.theme.Images
import cz.kudladev.vehicletracking.core.ui.tracking.TrackingItem
import cz.kudladev.vehicletracking.core.ui.tracking.TrackingItemSkeleton
import cz.kudladev.vehicletracking.model.Tracking
import cz.kudladev.vehicletracking.model.TrackingState
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import vehicletracking.feature.menu.manage_trackings.generated.resources.Res
import vehicletracking.feature.menu.manage_trackings.generated.resources.manageTrackingsActiveTitle
import vehicletracking.feature.menu.manage_trackings.generated.resources.manageTrackingsEmpty
import vehicletracking.feature.menu.manage_trackings.generated.resources.manageTrackingsEmptyDescription
import vehicletracking.feature.menu.manage_trackings.generated.resources.manageTrackingsError
import vehicletracking.feature.menu.manage_trackings.generated.resources.manageTrackingsHistoryTitle
import vehicletracking.feature.menu.manage_trackings.generated.resources.manageTrackingsNotStartedTitle
import vehicletracking.feature.menu.manage_trackings.generated.resources.manageTrackingsRequestedTitle

@Serializable
data class ManageTrackings(
    val type: ManageTrackingsTypes
)

@Serializable
enum class ManageTrackingsTypes(val states: List<TrackingState>, val title: StringResource){
    ACTIVE(
        states =listOf(
            TrackingState.ACTIVE
        ),
        title = Res.string.manageTrackingsActiveTitle
    ),
    NOT_STARTED(
        states = listOf(
            TrackingState.APPROVED
        ),
        title = Res.string.manageTrackingsNotStartedTitle
    ),
    REQUESTED(
        states = listOf(
            TrackingState.PENDING
        ),
        title = Res.string.manageTrackingsRequestedTitle
    ),
    HISTORY(
        states = listOf(
            TrackingState.COMPLETED,
            TrackingState.REJECTED,
            TrackingState.RETURNED,
            TrackingState.FAILED,
            TrackingState.ERROR
        ),
        title = Res.string.manageTrackingsHistoryTitle
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageTrackingsRoot(
    viewModel: ManageTrackingsViewModel = koinViewModel(),
    paddingValues: PaddingValues,
    bottomAppBarScrollBehavior: BottomAppBarScrollBehavior,
    onBack: () -> Unit,
    onTrackingClicked: (tracking: Tracking) -> Unit,
    type: ManageTrackingsTypes
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val trackings = viewModel.trackings.collectAsLazyPagingItems()

    ManageTrackingsScreen(
        paddingValues = paddingValues,
        state = state,
        bottomAppBarScrollBehavior = bottomAppBarScrollBehavior,
        type = type,
        trackings = trackings,
        onAction = viewModel::onAction,
        onBack = onBack,
        onTrackingClicked = onTrackingClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManageTrackingsScreen(
    paddingValues: PaddingValues,
    bottomAppBarScrollBehavior: BottomAppBarScrollBehavior,
    state: ManageTrackingsState,
    type: ManageTrackingsTypes,
    trackings: LazyPagingItems<Tracking>,
    onAction: (ManageTrackingsAction) -> Unit,
    onBack: () -> Unit,
    onTrackingClicked: (tracking: Tracking) -> Unit
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(type.title),
                        fontStyle = FontStyle.Italic,
                    )
                },
                navigationIcon = {
                    BackButton(onClick = onBack)
                },
                scrollBehavior = topAppBarScrollBehavior
            )
        },
        modifier = Modifier
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            .nestedScroll(bottomAppBarScrollBehavior.nestedScrollConnection),
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
                        text = stringResource(Res.string.manageTrackingsError),
                        modifier = Modifier.padding(combinedPadding)
                    )
                }
                LoadState.Loading -> {
                    Column(
                        modifier = Modifier
                            .padding(combinedPadding),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        repeat(6) {
                            TrackingItemSkeleton(modifier = Modifier.fillMaxWidth())
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
                                    contentDescription = stringResource(Res.string.manageTrackingsEmpty),
                                    modifier = Modifier.size(64.dp)
                                )
                                Text(
                                    text = stringResource(Res.string.manageTrackingsEmptyDescription),
                                    modifier = Modifier.padding(top = 8.dp),
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            }
                        }
                        false -> {
                            LazyColumn(
                                contentPadding = combinedPadding,
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(trackings.itemCount) { tracking ->
                                    val item = trackings[tracking]
                                    if (item != null) {
                                        TrackingItem(
                                            tracking = item,
                                            onClick = { onTrackingClicked(item) },
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
